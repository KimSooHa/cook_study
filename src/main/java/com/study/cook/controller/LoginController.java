package com.study.cook.controller;

import com.study.cook.SessionConst;
import com.study.cook.domain.Member;
import com.study.cook.dto.MemberLoginIdSearchCondition;
import com.study.cook.dto.MemberPwdSearchCondition;
import com.study.cook.service.LoginService;
import com.study.cook.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    private final LoginService loginService;

    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginForm") LoginForm form) {
        return "login/login-form";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute LoginForm form, BindingResult result,
                        @RequestParam(defaultValue = "/") String redirectURL,
                        HttpServletRequest request) {
        if (result.hasErrors()) {
            return "login/login-form";
        }

        Member loginMember = loginService.login(form.getLoginId(), form.getPwd());

        if (loginMember == null) {
            result.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login/login-form";
        }

        // 로그인 성공 처리

        // 세션이 있으면 있는 세션 반환, 없으면 신규 세션을 생성
        HttpSession session = request.getSession(); // 세션이 없으면 새로운 세션을 생성해서 반환한다.
        // 세션에 로그인 회원 정보 보관
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);

        return "redirect:" + redirectURL;
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        // 세션 삭제
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();   // 세션 제거
        }

        return "redirect:/";
    }

//    @GetMapping("/searchId")
//    public String searchLoginId(MemberLoginIdSearchCondition condition, Model model) {
//        Member findMember = memberService.findOne(condition);
//        String loginId = findMember.getLoginId();
//        model.addAttribute("loginId", loginId);
//        return "member/find-id";
//    }
//
//    @GetMapping("/searchPwd")
//    public String searchPwd(MemberPwdSearchCondition condition, Model model) {
//        Member findMember = memberService.findOne(condition);
//        String pwd = findMember.getPwd();
//        model.addAttribute("pwd", pwd);
//        return "member/find-pwd";
//    }
//
//    @GetMapping("/{memberId}/edit")
//    public String update(@PathVariable Long memberId, Model model) {
//        Member member = memberService.findOneById(memberId);
//
//        MemberForm form = new MemberForm();
//        form.setName(member.getName());
//        form.setLoginId(member.getLoginId());
//        form.setEmail(member.getEmail());
//        form.setPwd(member.getPwd());
//        form.setPhoneNum(member.getPhoneNum());
//
//        model.addAttribute("form", form);
//        return "/update-form";
//    }
//
//    @PostMapping("/{memberId}/edit")
//    public String update(@PathVariable Long memberId, @Valid MemberForm form, BindingResult result) {
//
//        if (result.hasErrors()) {
//            return "member/update-form";
//        }
//
//        memberService.update(memberId, form.getName(), form.getLoginId(), form.getPwd(), form.getEmail(), form.getPhoneNum());
//        return "redirect:/mypage";
//    }





}
