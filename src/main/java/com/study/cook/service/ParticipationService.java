package com.study.cook.service;

import com.study.cook.domain.Club;
import com.study.cook.domain.Member;
import com.study.cook.domain.Participation;
import com.study.cook.enums.ParticipateFailReason;
import com.study.cook.exception.FindClubException;
import com.study.cook.exception.ParticipateFailException;
import com.study.cook.repository.ClubRepository;
import com.study.cook.repository.ParticipationRepository;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

import static com.study.cook.enums.ClubStatus.COMP;
import static com.study.cook.enums.ClubStatus.POS;
import static com.study.cook.enums.ParticipateFailReason.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ParticipationService {

    private final ParticipationRepository participationRepository;

    private final ClubRepository clubRepository;
    private final MeterRegistry registry;

    @PostConstruct
    public void initFailCounters() {
        for (ParticipateFailReason reason : ParticipateFailReason.values()) {
            registry.counter("club.participation.fail", "reason", reason.name().toLowerCase());
        }
    }

    /**
     * 참여자 등록
     */
    @Transactional
    @CacheEvict(value = "popularClubListCache", allEntries = true)  // 인기 스터디 리스트 캐시 제거
    public Long tryToCreate(Long clubId, Member member) {

        Club club = clubRepository.findById(clubId).orElseThrow(() -> {
            registry.counter("club.participation.fail", "reason", "club_not_found").increment();
            throw new FindClubException("해당 쿡스터디가 더이상 존재하지 않습니다.", CLUB_NOT_FOUND);
        });
        // 참여중인 스터디인지 확인
        if (participationRepository.findByClubIdAndMemberId(clubId, member.getId()).isPresent()) {
            registry.counter("club.participation.fail", "reason", "already_joined").increment();
            throw new ParticipateFailException("이미 참여중인 스터디입니다.", ALREADY_JOINED);
        }

        // 현재 인원 수 기준으로 정원 초과 여부 판단
        Long participantCount = countByClub(clubId);
        if (participantCount >= club.getMaxCount()) {
            registry.counter("club.participation.fail", "reason", "club_full").increment();
            throw new ParticipateFailException("정원이 다 찼습니다.", CLUB_FULL);
        }

        // 참여 저장
        Participation participation = Participation.createParticipation(member, club);
        participationRepository.save(participation);

        // 참여 저장 후 상태 변경
        if (participantCount + 1 >= club.getMaxCount()) {
            club.setStatus(COMP);
        }

        registry.counter("club.participation.success").increment(); // 성공
        return participation.getId();
    }

    public Long countByClub(Long clubId) {
        return participationRepository.countByClubId(clubId);
    }

    public Optional<List<Participation>> findByMember(Member member) {
        return participationRepository.findByMember(member.getId());
    }

    public Participation findByClubAndMember(Club club, Member member) {
        return participationRepository.findByClubIdAndMemberId(club.getId(), member.getId()).orElseThrow(() -> new IllegalArgumentException("no such data"));
    }

    @Transactional
    @CacheEvict(value = "popularClubListCache", allEntries = true)  // 인기 스터디 리스트 캐시 제거
    public void delete(Participation participation) {
        participationRepository.delete(participation);
        Club club = participation.getClub();

        if (club.getStatus() == COMP || club.getMaxCount() > countByClub(club.getId()))
            club.setStatus(POS);
    }

}
