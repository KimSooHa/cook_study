package com.study.cook.service;

import com.study.cook.SessionConst;
import com.study.cook.controller.LoginForm;
import com.study.cook.domain.Member;
import com.study.cook.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Slf4j
class LoginServiceTest {

    @Autowired
    LoginService loginService;

    @Autowired
    MemberRepository memberRepository;

    private HttpSession session;

    @BeforeEach
    public void testSave() {
        Member member = new Member("testMember1", "test1", "testMember1234*", "testMember1@email.com", "010-1234-1231");
        memberRepository.save(member);
    }

//    @Test
//    @DisplayName("로그인")
//    void login() {
//        // given
//        String email = "testMember1@email.com";
//        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("no such data"));
//        LoginForm form = new LoginForm();
//        form.setLoginId(member.getLoginId());
//        form.setPwd(member.getPwd());
//
//        // when
//        session = new MockHttpSession();
//        loginService.login(form, session);
//
//        // then
//        assertThat(session.getAttribute(SessionConst.LOGIN_MEMBER)).isNotNull();
//    }

//    @Test
//    @DisplayName("로그아웃")
//    void logout() {
//        // when
//        loginService.logout(session);
//
//        // then
//        assertThat(session).isNull();
//    }
}