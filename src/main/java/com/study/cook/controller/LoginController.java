package com.study.cook.controller;

import com.study.cook.dto.RefreshTokenDto;
import com.study.cook.service.LoginService;
import com.study.cook.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    private final LoginService loginService;
    private final MemberService memberService;

    @GetMapping("/loginForm")
    public String loginForm(@ModelAttribute("loginForm") LoginForm form) {
        return "login/login-form";
    }

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
    public String logout(@RequestBody RefreshTokenDto refreshTokenDto) {

        loginService.logout(refreshTokenDto.getRefreshToken());

        return "redirect:/";
    }
}
