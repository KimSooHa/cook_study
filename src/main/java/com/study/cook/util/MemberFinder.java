package com.study.cook.util;

import com.study.cook.SessionConst;
import com.study.cook.auth.CustomUserDetails;
import com.study.cook.domain.Member;
import com.study.cook.dto.MemberSearchCondition;
import com.study.cook.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Component
@RequiredArgsConstructor
public class MemberFinder {

    private final MemberService memberService;

    // 로그인 회원 찾기
    public Member getMember(HttpSession session) {
//        HttpSession session = request.getSession(false);
        // 세션에 로그인 회원 정보 조회
        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        Member member = memberService.findOneById(loginMember.getId());
        return member;
    }

    public Member getMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Member member = null;
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails memberDetails = (CustomUserDetails) authentication.getPrincipal();
            // UserDetails에서 사용자 정보 확인
            String loginId = memberDetails.getUsername();
            String pwd = memberDetails.getPassword();
            MemberSearchCondition condition = new MemberSearchCondition();
            condition.setLoginId(loginId);
            condition.setPwd(pwd);
            member = memberService.findOneByLoginIdAndPwd(condition);
        }
        return member;
    }
}
