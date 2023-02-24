package com.study.cook.util;

import com.study.cook.SessionConst;
import com.study.cook.domain.Member;
import com.study.cook.service.MemberService;
import lombok.RequiredArgsConstructor;
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

}
