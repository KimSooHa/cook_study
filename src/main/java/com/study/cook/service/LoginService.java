package com.study.cook.service;

import com.study.cook.SessionConst;
import com.study.cook.controller.LoginForm;
import com.study.cook.domain.Member;
import com.study.cook.exception.LoginFailException;
import com.study.cook.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    private final RefreshTokenService refreshTokenService;

    /**
     * 로그인
     */
    // null이면 로그인 실패
//    public LoginResponseDto login(LoginForm form) {
//
//        // pwd가 없을 경우 예외 발생.
//        Member loginMember = memberRepository.findByLoginId(form.getLoginId())
//        .stream().filter(m -> passwordEncoder.matches(form.getPwd(), m.getPwd()))
//        .findAny().orElseThrow(() -> new LoginFailException("아이디 또는 비밀번호가 맞지 않습니다."));
//
//        // 로그인 성공 처리
//        // JWT토큰 생성. jwt 라이브러리를 이용하여 생성
//        String accessToken = jwtAuthenticationProvider.createAccessToken(loginMember.getId(), loginMember.getLoginId());
//        String refreshToken = jwtAuthenticationProvider.createRefreshToken(loginMember.getId(), loginMember.getLoginId());
//
//        // RefreshToken을 DB에 저장. 성능상 DB가 아니라 memory DB(Redis)에 저장하는 것이 좋다.
//        RefreshToken refreshTokenEntity = new RefreshToken(loginMember.getId(), refreshToken);
//        refreshTokenService.create(refreshTokenEntity);
//
//        return LoginResponseDto.builder()
//                .accessToken(accessToken)
//                .refreshToken(refreshToken)
//                .memberId(loginMember.getId())
//                .name(loginMember.getName())
//                .build();
//    }


    /**
     * 로그아웃
     */
    public void logout(String refreshToken) {
        refreshTokenService.delete(refreshToken);
    }


    /**
     * session 로그인
     * @param form
     * @param session
     */
    public void login(LoginForm form, HttpSession session) {

        Member loginMember = memberRepository.findByLoginId(form.getLoginId())
                .stream().filter(m -> m.getPwd().equals(form.getPwd()))
                .findAny().orElseThrow(() -> new LoginFailException("아이디 또는 비밀번호가 맞지 않습니다."));


        // 로그인 성공 처리

        // 세션에 로그인 회원 정보 보관
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);
    }

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
