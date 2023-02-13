package com.study.cook.controller;

import com.study.cook.SessionConst;
import com.study.cook.argumentresolver.Login;
import com.study.cook.domain.Member;
import com.study.cook.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
@Slf4j
@RequiredArgsConstructor
public class HomeController {

    private final MemberRepository memberRepository;

    @RequestMapping("/")
    public String home(@Login Member loginMember, Model model) {

        // 세션에 회원 데이터가 없으면 home
        if (loginMember == null) {
            return "home";
        }

        // 세션이 유지되면 로그인 홈으로 이동
        model.addAttribute("member", loginMember);
        return "login-home";
    }
}
