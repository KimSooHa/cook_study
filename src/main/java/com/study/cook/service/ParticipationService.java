package com.study.cook.service;

import com.study.cook.domain.*;
import com.study.cook.repository.CookingRoomRepository;
import com.study.cook.repository.ParticipationRepository;
import com.study.cook.repository.ReservationRepository;
import com.study.cook.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.study.cook.domain.ClubStatus.COMP;
import static com.study.cook.domain.ClubStatus.POS;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ParticipationService {

    private final CookingRoomRepository cookingRoomRepository;
    private final ReservationRepository reservationRepository;
    private final ParticipationRepository participationRepository;

    /**
     * 참여자 등록
     */
    @Transactional  // 변경해야 하기 때문에 읽기, 쓰기가 가능해야 함
    public Long create(Club club, Member member) {

        Participation participation = Participation.createParticipation(member, club);
        participationRepository.save(participation);

        Long participantCount = countByClub(club);
        if (club.getMaxCount() <= participantCount) {
            club.setStatus(COMP);
        }
            return participation.getId();
    }


    /**
     * 전체 조회
     */
    public List<Participation> findList() {
        return participationRepository.findAll();
    }

    /**
     * 단건 조회
     */
    public Participation findOneById(Long participationId) {
        return participationRepository.findById(participationId).orElse(null);
    }

    public Long countByClub(Club club) {
        return participationRepository.countByClubId(club.getId());
    }

    public Optional<List<Participation>> findByMember(Member member) {
        return participationRepository.findByMember(member.getId());
    }

    public Participation findByClubAndMember(Club club, Member member) {
        return participationRepository.findByClubIdAndMemberId(club.getId(), member.getId()).orElseThrow(() -> new IllegalArgumentException("no such data"));
    }


    @Transactional
    public void update(Long id, LocalDateTime startDateTime, LocalDateTime endDateTime, int cookingRoomNum) {
        Reservation reservation = reservationRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("no such data"));
        reservation.setStartDateTime(startDateTime);
        reservation.setEndDateTime(endDateTime);
        CookingRoom cookingRoom = cookingRoomRepository.findByRoomNum(cookingRoomNum).orElseThrow(() -> new IllegalArgumentException("no such data"));
        reservation.setCookingRoom(cookingRoom);
    }

    @Transactional
    public void delete(Participation participation) {
        participationRepository.delete(participation);
        Club club = participation.getClub();

        if(club.getStatus() == COMP || club.getMaxCount() > countByClub(club))
            club.setStatus(POS);
    }

}
