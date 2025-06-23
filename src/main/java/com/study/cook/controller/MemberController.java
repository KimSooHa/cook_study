package com.study.cook.controller;

import com.study.cook.auth.CustomUserDetails;
import com.study.cook.domain.Member;
import com.study.cook.dto.LoginMember;
import com.study.cook.exception.CheckMatchPwdException;
import com.study.cook.exception.CheckNewPwdException;
import com.study.cook.exception.FindMemberException;
import com.study.cook.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
@RequestMapping("/members")
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @GetMapping
    public String createForm(Model model) {
        model.addAttribute("memberForm", new MemberForm());
        return "member/create-form";
    }

    @PostMapping
    public String create(Model model, @Valid MemberForm form, BindingResult result) {
        if (result.hasErrors()) {
            log.info("errors={}", result);
            return "member/create-form";
        }

        try {
            memberService.join(form);
        } catch (IllegalStateException e) {

            model.addAttribute("msg", e.getMessage());
            model.addAttribute("url", "/members");
            return "member/create-form";
        }

        log.info("회원가입 성공!");
        model.addAttribute("msg", "회원가입되었습니다!");
        model.addAttribute("url", "/");
        return "member/create-form";
    }

    @GetMapping("/{memberId}")
    public String update(@PathVariable Long memberId, Model model) {

        Member member = memberService.findOneById(memberId);

        MemberUpdateForm form = new MemberUpdateForm();
        form.setName(member.getName());
        form.setLoginId(member.getLoginId());
        form.setEmail(member.getEmail());
        // 현재 비밀번호와 변경할 비밀번호 비교때문에 세팅x
//        form.setPwd(member.getPwd());
        form.setPhoneNum(member.getPhoneNum());

        model.addAttribute("memberForm", form);
        model.addAttribute("memberId", memberId);
        return "member/update-form";
    }

    @PutMapping("/{memberId}")
    public String update(@PathVariable Long memberId, @Valid MemberUpdateForm form, BindingResult result, RedirectAttributes redirectAttributes, Model model) {

        if (result.hasErrors()) {
            log.info("errors={}", result);
            return resetPwd(form, model);
        }
        try {
            memberService.update(memberId, form);
        } catch (FindMemberException e) {
            return resetPwdAndAddMsg(form, model, e.getMessage(), "msg");
        } catch (CheckMatchPwdException e) {
            return resetPwdAndAddMsg(form, model, e.getMessage(), "currentPwdError");
        } catch (CheckNewPwdException e) {
            return resetPwdAndAddMsg(form, model, e.getMessage(), "newPwdError");
        }

        // 세션 정보 갱신
        Member updatedMember = memberService.findOneById(memberId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        CustomUserDetails newPrincipal = new CustomUserDetails(updatedMember);
        CustomUserDetails newPrincipal = new CustomUserDetails(new LoginMember(updatedMember));
        Authentication newAuthentication = new UsernamePasswordAuthenticationToken(newPrincipal, authentication.getCredentials(), authentication.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(newAuthentication);

        redirectAttributes.addFlashAttribute("msg", "수정하였습니다.");
        return "redirect:/mypage";
    }

    private static String resetPwdAndAddMsg(MemberUpdateForm form, Model model, String e, String msg) {
        model.addAttribute(msg, e);
        return resetPwd(form, model);
    }

    private static String resetPwd(MemberUpdateForm form, Model model) {
        form.setCurrentPwd("");
        form.setNewPwd("");
        form.setNewPwdConfirm("");
        model.addAttribute("memberForm", form);
        return "member/update-form";
    }


    // 탈퇴
    @DeleteMapping("/{memberId}")
    public String delete(@PathVariable Long memberId, Model model) {
        memberService.delete(memberId);
        model.addAttribute("msg", "탈퇴되었습니다.");
        model.addAttribute("url", "/logout"); // 회원 탈퇴 후 로그아웃 처리
        return "mypage/index";
    }
}
