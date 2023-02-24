package com.study.cook.service;

import com.study.cook.controller.ClubForm;
import com.study.cook.domain.*;
import com.study.cook.dto.ClubListDto;
import com.study.cook.dto.RecipeSearchCondition;
import com.study.cook.dto.ReservationDto;
import com.study.cook.repository.ClubRepository;
import com.study.cook.util.MemberFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ClubService {

    private final ClubRepository clubRepository;
    private final MemberService memberService;
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

        List<Reservation> reservations = new ArrayList<>();
        for (Long id : form.getReservationIds()) {
            Reservation reservation = reservationService.findOneById(id);
            reservations.add(reservation);
        }

        Club createdClub = Club.createClub(club, member, category, reservations);

        clubRepository.save(createdClub);

        // 쿡스터디 참여
        participationService.create(club, member);

        return club.getId();
    }


    /**
     * 그룹 전체 조회
     */
    public Page<ClubListDto> findList(RecipeSearchCondition condition ,Pageable pageable) {
        return clubRepository.findList(condition, pageable);
    }

    /**
     * 그룹 조회
     */
    public Club findOneById(Long clubId) {
        return clubRepository.findById(clubId).orElse(null);
    }

    /**
     * 그룹 리스트 조회
     */
//    public Member findOne(MemberLoginIdSearchCondition condition) {
//        return clubRepository.findByEmailAndPhoneNum(condition.getEmail(), condition.getPhoneNum());
//    }
//
//    public Member findOne(MemberPwdSearchCondition condition) {
//        return clubRepository.findByLoginIdAndEmail(condition.getLoginId(), condition.getEmail());
//    }
    public Page<ClubListDto> findByParticipant(Long memberId, Pageable pageable) {
        return clubRepository.findByParticipant(memberId, pageable);
    }

    public Page<ClubListDto> findByMember(Long memberId, Pageable pageable) {
        return clubRepository.findByMemberId(memberId, pageable);
    }

    @Transactional
    public void update(Long clubId, ClubForm form) {
        Club club = clubRepository.findById(clubId).orElseThrow(() -> new IllegalArgumentException("no such data"));
        club.setName(form.getName());
        club.setIntroduction(form.getIntroduction());
        club.setMaxCount(form.getMaxCount());
        Category category = categoryService.findOneById(form.getCategoryId());
        club.setCategory(category);
        club.setIngredients(form.getIngredients());

        List<Reservation> reservations = new ArrayList<>();
        if (form.getReservationIds().isEmpty()) {
            List<Reservation> findReservations = club.getReservations();
            for (Reservation findReservation : findReservations) {
                findReservation.setClub(null);
            }

        }

        else {
            for (Long id : form.getReservationIds()) {
                Reservation reservation = reservationService.findOneById(id);
                reservations.add(reservation);
            }
            club.setReservations(reservations);
        }

    }

    @Transactional
    public void delete(Long clubId) {
        Club club = findOneById(clubId);
        clubRepository.delete(club);
    }
}
