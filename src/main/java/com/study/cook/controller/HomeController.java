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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
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
    public String myPage(Model model, HttpSession session) {
//        Member member = memberFinder.getMember(session);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails memberDetails = (CustomUserDetails) authentication.getPrincipal();
            // UserDetails에서 사용자 정보 확인
            String loginId = memberDetails.getUsername();
            String pwd = memberDetails.getPassword();
            MemberSearchCondition condition = new MemberSearchCondition();
            condition.setLoginId(loginId);
            condition.setPwd(pwd);
            Member member = memberService.findOneByLoginIdAndPwd(condition);
            // 추가적인 사용자 정보 가져오기 (사용자가 구현한 UserDetails 클래스에 따라 다를 수 있음)
            // 예: userDetails.getAuthorities(), userDetails.getPassword(), 등

            model.addAttribute("memberId", member.getId());
        }
        return "mypage/index";
    }
}
