package com.study.cook.controller;

import com.study.cook.domain.Member;
import com.study.cook.dto.MemberLoginIdSearchCondition;
import com.study.cook.dto.MemberPwdSearchCondition;
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
@RequestMapping("/members")
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("memberForm", new MemberForm());
        return "member/create-member-form";
    }

    @PostMapping("/new")
    public String create(@Valid MemberForm form, BindingResult result) {
        if (result.hasErrors()) {
            return "member/create-member-form";
        }

        Member member = new Member(form.getName(), form.getLoginId(), form.getPwd(), form.getEmail(), form.getPhoneNum());

        memberService.join(member);
        return "redirect:/";
    }

    @GetMapping("/searchId")
    public String searchLoginId(MemberLoginIdSearchCondition condition, Model model) {
        Member findMember = memberService.findOne(condition);
        String loginId = findMember.getLoginId();
        model.addAttribute("loginId", loginId);
        return "member/find-id";
    }

    @GetMapping("/searchPwd")
    public String searchPwd(MemberPwdSearchCondition condition, Model model) {
        Member findMember = memberService.findOne(condition);
        String pwd = findMember.getPwd();
        model.addAttribute("pwd", pwd);
        return "member/find-pwd";
    }

    @GetMapping("/{memberId}/edit")
    public String update(@PathVariable Long memberId, Model model) {
        Member member = memberService.findOneById(memberId);

        MemberForm form = new MemberForm();
        form.setName(member.getName());
        form.setLoginId(member.getLoginId());
        form.setEmail(member.getEmail());
        form.setPwd(member.getPwd());
        form.setPhoneNum(member.getPhoneNum());

        model.addAttribute("form", form);
        return "/update-form";
    }

    @PostMapping("/{memberId}/edit")
    public String update(@PathVariable Long memberId, @Valid MemberForm form, BindingResult result) {

        if (result.hasErrors()) {
            return "member/update-form";
        }
        
        memberService.update(memberId, form.getName(), form.getLoginId(), form.getPwd(), form.getEmail(), form.getPhoneNum());
        return "redirect:/mypage";
    }





}
