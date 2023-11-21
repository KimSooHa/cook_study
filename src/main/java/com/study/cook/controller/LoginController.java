package com.study.cook.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
@RequiredArgsConstructor
@Slf4j
public class LoginController {

//    private final LoginService loginService;
//    private final MemberService memberService;

    @GetMapping("/loginForm")
    public String loginForm(@ModelAttribute("loginForm") LoginForm form) {
        return "login/login-form";
    }

    /**
     * login, logout - spring security의 form login을 통해 처리로 구현 변경
     */
//    @PostMapping("/login")
//    public String login(Model model, @Valid @ModelAttribute LoginForm form, BindingResult result,
//                        @RequestParam(defaultValue = "/") String redirectURL) {
//        if (result.hasErrors())
//            return "login/login-form";
//
//        try {
//            loginService.login(form, session);
//        } catch (LoginFailException e) {
//            result.reject("loginFail", e.getMessage());
//            model.addAttribute("msg", e.getMessage());
//            return "login/login-form";
//        }
//
//        // 로그인 성공 처리
//        model.addAttribute("url", redirectURL);
//        return "login/login-form";
//    }

//    @PostMapping("/logout")
//    public String logout(Session session) {
//
//        loginService.logout(session);
//
//        return "redirect:/";
//    }
}
