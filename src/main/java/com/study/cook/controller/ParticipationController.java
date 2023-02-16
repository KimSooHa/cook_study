package com.study.cook.controller;

import com.study.cook.SessionConst;
import com.study.cook.domain.Club;
import com.study.cook.domain.ClubStatus;
import com.study.cook.domain.Member;
import com.study.cook.domain.Participation;
import com.study.cook.service.ClubService;
import com.study.cook.service.MemberService;
import com.study.cook.service.ParticipationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Controller
@RequiredArgsConstructor
@RequestMapping("/clubs")
@Slf4j
public class ParticipationController {

    private final MemberService memberService;
    private final ParticipationService participationService;
    private final ClubService clubService;


    /**
     * 쿡스터디 참여
     */

    @PostMapping("/{clubId}/participations")
    public String reserve(@Valid ReservationForm form, @PathVariable Long clubId, BindingResult result, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "reservation/create-form";
        }
        Member member = getMember(request);
        Club club = clubService.findOneById(clubId);

        Participation participation = Participation.createParticipation(member, club);
        Long participantCount = participationService.countByClub(club);

        if (club.getMaxCount() > participantCount) {
            participationService.create(participation);
        } else {
            club.setStatus(ClubStatus.COMP);
            redirectAttributes.addAttribute("status", false);
        }


        return "redirect:/clubs/{clubId}";
    }


    @DeleteMapping("/{clubId}/participations")
    public String delete(@PathVariable Long clubId, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        Member member = getMember(request);
        Club club = clubService.findOneById(clubId);
        Participation participation = participationService.findByClubAndMember(club, member);
        participationService.delete(participation);

        redirectAttributes.addAttribute("clubId", clubId);
        return "redirect:/clubs/{clubId}";
    }

    private static LocalDateTime parseToDateTime(LocalDate date, LocalTime time) {
        LocalDateTime dateTime = LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), time.getHour(), time.getMinute());
        return dateTime;
    }


    // 로그인 회원 찾기
    private Member getMember(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        // 세션에 로그인 회원 정보 조회
        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        Member member = memberService.findOneById(loginMember.getId());
        return member;
    }

}
