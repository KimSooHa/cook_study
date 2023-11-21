package com.study.cook.service;

import org.springframework.stereotype.Service;

/**
 * spring security의 form login을 통해 로그인, 로그아웃 처리로 수정
 */
@Service
//@RequiredArgsConstructor
public class LoginService {

//    private final MemberRepository memberRepository;


    /**
     * session 로그인
     * @param form
     * @param session
     */
//    public void login(LoginForm form, HttpSession session) {
//
//        Member loginMember = memberRepository.findByLoginId(form.getLoginId())
//                .stream().filter(m -> m.getPwd().equals(form.getPwd()))
//                .findAny().orElseThrow(() -> new LoginFailException("아이디 또는 비밀번호가 맞지 않습니다."));
//
//
//        // 로그인 성공 처리
//
//        // 세션에 로그인 회원 정보 보관
//        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);
//    }

    /**
     * session logout
     * @param session
     */
//    public void logout(HttpSession session) {
//        // 세션 삭제
//        if (session != null) {
//            session.invalidate();   // 세션 제거
//        }
//    }

}
