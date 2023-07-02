package com.study.cook.service;

import com.study.cook.SessionConst;
import com.study.cook.controller.LoginForm;
import com.study.cook.controller.MemberForm;
import com.study.cook.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
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
    MemberService memberService;

    private HttpSession session;

    @BeforeEach
    public void testSave() {
        MemberForm form = new MemberForm();
        form.setName("testMember1");
        form.setLoginId("test1");
        form.setPwd("testMember1234*");
        form.setEmail("testMember1@email.com");
        form.setPhoneNum("010-1234-1231");
        memberService.join(form);
    }

    @Test
    @DisplayName("로그인")
    void login() {
        // given
        Member member = memberService.findOneByEmail("testMember1@email.com");
        LoginForm form = new LoginForm();
        form.setLoginId(member.getLoginId());
        form.setPwd(member.getPwd());

        // when
        session = new MockHttpSession();
        loginService.login(form, session);

        // then
        assertThat(session.getAttribute(SessionConst.LOGIN_MEMBER)).isNotNull();
    }

    @Test
    @DisplayName("로그아웃")
    void logout() {
        // when
        loginService.logout(session);

        // then
        assertThat(session).isNull();
    }
}