package com.study.cook.service;

import com.study.cook.controller.ClubForm;
import com.study.cook.domain.Category;
import com.study.cook.domain.Club;
import com.study.cook.domain.Member;
import com.study.cook.domain.Reservation;
import com.study.cook.dto.ClubListDto;
import com.study.cook.dto.SearchCondition;
import com.study.cook.exception.FindClubException;
import com.study.cook.repository.ClubRepository;
import com.study.cook.util.MemberFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ClubService {

    private final ClubRepository clubRepository;
    private final CategoryService categoryService;
    private final ReservationService reservationService;
    private final ParticipationService participationService;
    private final MemberFinder memberFinder;


    /**
     * 클럽 등록
     */
    @Transactional  // 변경해야 하기 때문에 읽기, 쓰기가 가능해야 함
    public Long create(ClubForm form, HttpSession session) {

        Club club = new Club(form.getName(), form.getIntroduction(), form.getMaxCount(), form.getIngredients());

        Member member = memberFinder.getMember(session);
        Category category = categoryService.findOneById(form.getCategoryId());

        List<Reservation> reservations = form.getReservationIds().stream().map(id -> {
            return reservationService.findOneById(id);
        }).collect(toList());

        Club createdClub = Club.createClub(club, member, category, reservations);

        clubRepository.save(createdClub);

        // 쿡스터디 참여
        participationService.tryToCreate(club.getId(), member);

        return club.getId();
    }


    /**
     * 그룹 전체 조회
     */
    public Page<ClubListDto> findList(SearchCondition condition, Pageable pageable) {
        return clubRepository.findList(condition, pageable);
    }

    /**
     * 정해진 갯수의 그룹 리스트 조회(참여자 많은 순)
     */
    public List<ClubListDto> findLimitList(int length) {
        return clubRepository.findList(length);
    }

    /**
     * 그룹 조회
     */
    public Club findOneById(Long clubId) {
        return clubRepository.findById(clubId).orElseThrow(() -> new FindClubException("해당 쿡스터디가 존재하지 않습니다."));
    }

    /**
     * 참여하는 그룹 리스트 조회
     */
    public Page<ClubListDto> findByParticipant(Long memberId, SearchCondition condition, Pageable pageable) {
        return clubRepository.findByParticipant(memberId, condition, pageable);
    }

    /**
     * 등록한 그룹 리스트 조회
     */
    public Page<ClubListDto> findByMember(Long memberId, SearchCondition condition, Pageable pageable) {
        return clubRepository.findByMemberId(memberId, condition, pageable);
    }

    @Transactional
    public void update(Long clubId, ClubForm form) {
        Club club = clubRepository.findById(clubId).orElseThrow(() -> new FindClubException("수정 실패: 해당 쿡스터디가 존재하지 않습니다."));
        club.setName(form.getName());
        club.setIntroduction(form.getIntroduction());
        club.setMaxCount(form.getMaxCount());
        Category category = categoryService.findOneById(form.getCategoryId());
        club.setCategory(category);
        club.setIngredients(form.getIngredients());

        List<Reservation> findReservations = club.getReservations();
        findReservations.stream().forEach(fr -> fr.setClub(null));

        if (!(form.getReservationIds().isEmpty())) {
            form.getReservationIds().stream().forEach(id -> {
                Reservation reservation = reservationService.findOneById(id);
                club.addReservation(reservation);
            });
        }

    }

    @Transactional
    public void delete(Long clubId) {
        Club club = findOneById(clubId);
        clubRepository.delete(club);
    }
}
