package com.study.cook.controller;

import com.study.cook.domain.Club;
import com.study.cook.domain.Member;
import com.study.cook.domain.Participation;
import com.study.cook.exception.FindClubException;
import com.study.cook.exception.ParticipateFailException;
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
    public String reserve(@PathVariable Long clubId, RedirectAttributes redirectAttributes) {
        Member member = memberFinder.getMember();
        redirectAttributes.addAttribute("clubId", clubId);

        try {
            synchronized (this) {
                participationService.tryToCreate(clubId, member);
            }
        } catch (FindClubException e) {
            redirectAttributes.addFlashAttribute("msg", e.getMessage());
            return "redirect:/clubs/list";
        } catch (ParticipateFailException e) {
            redirectAttributes.addFlashAttribute("msg", e.getMessage());
            return "redirect:/clubs/{clubId}/detail";
        }

        // 참여 성공
        redirectAttributes.addFlashAttribute("msg", "참여되었습니다.");
        return "redirect:/clubs/{clubId}/detail";
    }


    /**
     * 쿡스터디 탈퇴
     */
    @DeleteMapping("/{clubId}/participations")
    public String delete(@PathVariable Long clubId, RedirectAttributes redirectAttributes) {
        Member member = memberFinder.getMember();
        Club club = clubService.findOneById(clubId);
        Participation participation = participationService.findByClubAndMember(club, member);
        participationService.delete(participation);

        redirectAttributes.addFlashAttribute("msg", "탈퇴되었습니다.");
        return "redirect:/clubs/{clubId}/detail";
    }

}
