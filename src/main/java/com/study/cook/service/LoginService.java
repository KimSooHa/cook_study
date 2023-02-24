package com.study.cook.service;

import com.study.cook.SessionConst;
import com.study.cook.controller.LoginForm;
import com.study.cook.controller.MemberForm;
import com.study.cook.domain.Member;
import com.study.cook.exception.LoginFailException;
import com.study.cook.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final MemberRepository memberRepository;

    // null이면 로그인 실패
    public void login(LoginForm form, HttpSession session) {

        Member loginMember = memberRepository.findByLoginId(form.getLoginId())
                .stream().filter(m -> m.getPwd().equals(form.getPwd()))
                .findAny().orElseThrow(() -> new LoginFailException("아이디 또는 비밀번호가 맞지 않습니다."));


        // 로그인 성공 처리

        // 세션에 로그인 회원 정보 보관
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);
    }

    public void logout(HttpSession session) {
        // 세션 삭제
        if (session != null) {
            session.invalidate();   // 세션 제거
        }
    }

}
