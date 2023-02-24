package com.study.cook.controller;

import com.study.cook.SessionConst;
import com.study.cook.domain.Member;
import com.study.cook.dto.MemberLoginIdSearchCondition;
import com.study.cook.dto.MemberPwdSearchCondition;
import com.study.cook.exception.LoginFailException;
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
    public String login(Model model, @Valid @ModelAttribute LoginForm form, BindingResult result,
                        @RequestParam(defaultValue = "/") String redirectURL,
                        HttpSession session) {
        if (result.hasErrors()) {
            return "login/login-form";
        }

        try {
            loginService.login(form, session);
        } catch (LoginFailException e) {
            result.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            model.addAttribute("msg", e.getMessage());
            return "login/login-form";
        }

        // 로그인 성공 처리

        return "redirect:" + redirectURL;
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {

        loginService.logout(session);

        return "redirect:/";
    }
}
