package com.study.cook.controller;

import com.study.cook.domain.Club;
import com.study.cook.domain.ClubStatus;
import com.study.cook.domain.Member;
import com.study.cook.domain.Participation;
import com.study.cook.service.ClubService;
import com.study.cook.service.ParticipationService;
import com.study.cook.util.MemberFinder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
@RequestMapping("/clubs")
@Slf4j
public class ParticipationController {

    private final ParticipationService participationService;
    private final ClubService clubService;
    private final MemberFinder memberFinder;


    /**
     * 쿡스터디 참여
     */

    @PostMapping("/{clubId}/participations")
    public String reserve(@PathVariable Long clubId, HttpSession session, RedirectAttributes redirectAttributes) {

        Member member = memberFinder.getMember(session);
        Club club = clubService.findOneById(clubId);


        // 인원이 다 차지 않았으면 참여
        if (club.getStatus() == ClubStatus.POS) {
            participationService.create(club, member);
            // 참여 성공
            redirectAttributes.addFlashAttribute("msg", "참여되었습니다.");
        } else {
            redirectAttributes.addFlashAttribute("msg", "정원이 다 찼습니다.");
        }


        redirectAttributes.addAttribute("clubId", clubId);
        return "redirect:/clubs/{clubId}/detail";
    }


    /**
     * 쿡스터디 탈퇴
     */
    @DeleteMapping("/{clubId}/participations")
    public String delete(@PathVariable Long clubId, RedirectAttributes redirectAttributes, HttpSession session) {
        Member member = memberFinder.getMember(session);
        Club club = clubService.findOneById(clubId);
        Participation participation = participationService.findByClubAndMember(club, member);
        participationService.delete(participation);

        redirectAttributes.addFlashAttribute("msg", "탈퇴되었습니다.");
        return "redirect:/clubs/{clubId}/detail";
    }

}
