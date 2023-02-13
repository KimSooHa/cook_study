package com.study.cook.controller;

import com.study.cook.domain.Category;
import com.study.cook.domain.Club;
import com.study.cook.domain.Member;
import com.study.cook.dto.MemberLoginIdSearchCondition;
import com.study.cook.dto.MemberPwdSearchCondition;
import com.study.cook.service.CategoryService;
import com.study.cook.service.ClubService;
import com.study.cook.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cookstudies")
@Slf4j
public class CookStudyController {

    private final ClubService clubService;
    private final MemberService memberService;
    private final CategoryService categoryService;


    @GetMapping("/clubs/{clubId}")
    public String detail(@PathVariable Long clubId, Model model) {
        Club club = clubService.findOneById(clubId);
        // 남은 인원수 조회 로직 추가하기
//        club.getMaxCount() -
//        ClubDto clubDto = new ClubDto(club.getName(),club.getIntroduction(), club.getStatus(), club.getMaxCount(), club.);


        Member member = memberService.findOneById(club.getMember().getId());
        Category category = categoryService.findOneById(club.getCategory().getId());
//        model.addAttribute("clubDto", clubDto);
        model.addAttribute("memberName", member.getName());
        model.addAttribute("categoryName", category.getName());

        return "";
    }

    /**
     *
     * 요리실 예약
     */
//    @GetMapping("/cooking-rooms")
//    public String reserveForm(Model model) {
//
//    }

    @GetMapping("/clubs")
    public String createClubForm(Model model) {
        model.addAttribute("clubForm", new ClubForm());
        return "club/create-form";
    }

    @PostMapping("/clubs")
    public String createClub(@Valid ClubForm form, BindingResult result) {
        if (result.hasErrors()) {
            return "club/create-form";
        }

        Club club = new Club(form.getName(), form.getIntroduction(), form.getMaxCount(), form.getIngredients());

        Member member = memberService.findOneById(form.getMemberId());
        Category category = categoryService.findOneById(form.getCategoryId());
        Club createdClub = Club.createClub(club, member, category);

        clubService.create(createdClub);
        return "redirect:/clubs/{clubId}/detail";
    }

    @GetMapping("/search-id")
    public String searchLoginId(MemberLoginIdSearchCondition condition, Model model) {
        Member findMember = memberService.findOne(condition);
        String loginId = findMember.getLoginId();
        model.addAttribute("loginId", loginId);
        return "member/find-id";
    }

    @GetMapping("/search-pwd")
    public String searchPwd(MemberPwdSearchCondition condition, Model model) {
        Member findMember = memberService.findOne(condition);
        String pwd = findMember.getPwd();
        model.addAttribute("pwd", pwd);
        return "member/find-pwd";
    }

    @GetMapping("/{clubId}/edit")
    public String update(@PathVariable Long clubId, Model model) {
        Club club = clubService.findOneById(clubId);

        ClubForm form = new ClubForm();
        form.setName(club.getName());
        form.setIntroduction(club.getIntroduction());
        form.setMaxCount(club.getMaxCount());
        form.setIngredients(club.getIngredients());
        form.setMemberId(club.getMember().getId());
        form.setCategoryId(club.getCategory().getId());

        model.addAttribute("form", form);
        return "/update-form";
    }

    @PutMapping("/{clubId}")
    public String update(@PathVariable Long memberId, @Valid MemberForm form, BindingResult result) {

        if (result.hasErrors()) {
            return "member/update-form";
        }
        memberService.update(memberId, form.getName(), form.getLoginId(), form.getPwd(), form.getEmail(), form.getPhoneNum());
        return "redirect:/mypage";
    }

    @DeleteMapping("/{clubId}")
    public String delete(@PathVariable Long clubId) {
        Club club = clubService.findOneById(clubId);
        clubService.delete(club);

        return "redirect:/";
    }





}
