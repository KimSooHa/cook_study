package com.study.cook.controller;

import com.study.cook.argumentresolver.Login;
import com.study.cook.domain.Member;
import com.study.cook.dto.ClubListDto;
import com.study.cook.service.ClubService;
import com.study.cook.util.MemberFinder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class HomeController {


    private final ClubService clubService;
    private final MemberFinder memberFinder;

    @GetMapping("/")
    public String home(@Login Member loginMember, Model model) {

        List<ClubListDto> list = clubService.findLimitList(4);
        model.addAttribute("list", list);


        // 세션에 회원 데이터가 없으면 home
        if (loginMember == null) {
            return "home";
        }

        // 세션이 유지되면 로그인 홈으로 이동
        model.addAttribute("member", loginMember);
        return "login-home";
    }

    @GetMapping("/mypage")
    public String myPage(Model model, HttpSession session) {
        Member member = memberFinder.getMember(session);
        model.addAttribute("memberId", member.getId());

        return "mypage/index";
    }
}
