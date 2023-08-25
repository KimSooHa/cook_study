package com.study.cook.controller;

import com.study.cook.domain.Member;
import com.study.cook.dto.MemberLoginIdSearchCondition;
import com.study.cook.dto.MemberPwdSearchCondition;
import com.study.cook.dto.MemberSignupResponseDto;
import com.study.cook.dto.RefreshTokenDto;
import com.study.cook.service.LoginService;
import com.study.cook.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
@RequestMapping("/members")
@Slf4j
public class MemberController {

    private final MemberService memberService;
    private final LoginService loginService;

    @GetMapping
    public String createForm(Model model) {
        model.addAttribute("memberForm", new MemberForm());
        return "member/create-form";
    }

    @PostMapping
    public String create(Model model, @Valid MemberForm form, BindingResult result, HttpServletResponse response) {
        if (result.hasErrors()) {
            log.info("errors={}", result);
            return "member/create-form";
        }

        Long memberId;
        try {
            memberId = memberService.join(form);
        } catch (IllegalStateException e) {

            model.addAttribute("msg", e.getMessage());
            model.addAttribute("url", "/members");
            return "member/create-form";
        }

        Member member = memberService.findOneById(memberId);

        MemberSignupResponseDto memberSignupResponse = new MemberSignupResponseDto();
        memberSignupResponse.setMemberId(memberId);
        memberSignupResponse.setName(member.getName());
        memberSignupResponse.setEmail(member.getEmail());
        memberSignupResponse.setRegdate(member.getRegDate());
        log.info("회원가입 성공!");
        model.addAttribute("signupResponse", memberSignupResponse);
        model.addAttribute("msg", "회원가입되었습니다!");
        model.addAttribute("url", "/");
        return "member/create-form";
    }

    @GetMapping("/{memberId}")
    public String update(@PathVariable Long memberId, Model model) {

        Member member = memberService.findOneById(memberId);

        MemberForm form = new MemberForm();
        form.setName(member.getName());
        form.setLoginId(member.getLoginId());
        form.setEmail(member.getEmail());
        form.setPwd(member.getPwd());
        form.setPhoneNum(member.getPhoneNum());

        model.addAttribute("memberForm", form);
        model.addAttribute("memberId", memberId);
        return "member/update-form";
    }

    @PutMapping("/{memberId}")
    public String update(@PathVariable Long memberId, @Valid MemberForm form, BindingResult result, RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            log.info("errors={}", result);
            return "member/update-form";
        }
        try {
            memberService.update(memberId, form);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("msg", "수정 실패: 해당 회원을 찾을 수 없습니다.");
            return "member/update-form";
        }
        redirectAttributes.addFlashAttribute("msg", "수정하였습니다.");
        return "redirect:/mypage";
    }

    // 탈퇴
    @DeleteMapping("/{memberId}")
    public String delete(@PathVariable Long memberId, HttpSession session, @RequestBody RefreshTokenDto refreshTokenDto, Model model) {
        memberService.delete(memberId);
//        loginService.logout(session);
        loginService.logout(refreshTokenDto.getRefreshToken());
        model.addAttribute("msg", "탈퇴되었습니다.");
        model.addAttribute("url", "/");
        return "mypage/index";
    }
}
