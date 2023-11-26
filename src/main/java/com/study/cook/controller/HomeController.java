package com.study.cook.controller;

import com.study.cook.auth.CustomUserDetails;
import com.study.cook.domain.Member;
import com.study.cook.dto.ClubListDto;
import com.study.cook.dto.MemberSearchCondition;
import com.study.cook.dto.RecipeListDto;
import com.study.cook.service.ClubService;
import com.study.cook.service.MemberService;
import com.study.cook.service.RecipeService;
import com.study.cook.util.MemberFinder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class HomeController {

    private final MemberService memberService;
    private final ClubService clubService;
    private final RecipeService recipeService;
    private final MemberFinder memberFinder;

//    @GetMapping("/")
//    public String home(@Login Member loginMember, Model model) {
//
//        List<ClubListDto> clubList = clubService.findLimitList(4);
//        List<RecipeListDto> recipeList = recipeService.findLimitList(4);
//        model.addAttribute("clubList", clubList);
//        model.addAttribute("recipeList", recipeList);
//
//
//        // 세션에 회원 데이터가 없으면 home
//        if (loginMember == null) {
//            return "home";
//        }
//
//        // 세션이 유지되면 로그인 홈으로 이동
//        model.addAttribute("member", loginMember);
//        return "login-home";
//    }

    @GetMapping("/")
    public String home(@AuthenticationPrincipal CustomUserDetails userDetails,  Model model) {

        List<ClubListDto> clubList = clubService.findLimitList(4);
        List<RecipeListDto> recipeList = recipeService.findLimitList(4);
        model.addAttribute("clubList", clubList);
        model.addAttribute("recipeList", recipeList);
        log.info("userDetails = {}", userDetails);
        if(userDetails != null) {
            MemberSearchCondition condition = new MemberSearchCondition();
            log.info("loginId = {}, pwd = {}", userDetails.getUsername(), userDetails.getPassword());
            condition.setLoginId(userDetails.getUsername());
            condition.setPwd(userDetails.getPassword());
            Member loginMember = memberService.findOneByLoginIdAndPwd(condition);
            if (loginMember != null) {
                model.addAttribute("member", loginMember);
//                return "login-home";
            }
        }

            return "home";
    }

    @GetMapping("/mypage")
    public String myPage(Model model) {
        Member member = memberFinder.getMember();
        model.addAttribute("member", member);
        return "mypage/index";
    }
}
