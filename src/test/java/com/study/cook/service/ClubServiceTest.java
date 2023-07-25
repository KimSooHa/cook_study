package com.study.cook.service;

import com.study.cook.SessionConst;
import com.study.cook.controller.ClubForm;
import com.study.cook.domain.Member;
import com.study.cook.repository.CategoryRepository;
import com.study.cook.repository.ClubRepository;
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
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Slf4j
class ClubServiceTest {

    @Autowired
    ClubService clubService;

    @Autowired
    ClubRepository clubRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    MemberRepository memberRepository;

    private HttpSession session;

    @BeforeEach
    public void testSave() {
        Member member = new Member("testMember1", "test1", "testMember1234*", "testMember1@email.com", "010-1234-1231");
        memberRepository.save(member);
    }

    @Test
    @DisplayName("클럽 등록")
    void create() {
        // given
        ClubForm form = setForm();
        HttpSession session = setSession();

        // when
        Long clubId = clubService.create(form, this.session);

        // then
        assertThat(clubRepository.findById(clubId).get().getName()).isEqualTo(form.getName());
    }

    @Test
    void findList() {
    }

    @Test
    void findLimitList() {
    }

    @Test
    void findOneById() {
    }

    @Test
    void findByParticipant() {
    }

    @Test
    void findByMember() {
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }

    private HttpSession setSession() {
        session = new MockHttpSession();
        Member member = memberRepository.findByLoginId("test1").get();
        session.setAttribute(SessionConst.LOGIN_MEMBER, member);
        return session;
    }

    private ClubForm setForm() {
        ClubForm form = new ClubForm();
        form.setName("테스트용 쿡스터디 모집");
        form.setIntroduction("테스트");
        form.setMaxCount(5);
        form.setIngredients("추후 공지");
        form.setCategoryId(categoryRepository.findAll().get(0).getId());
        form.setReservationIds(new ArrayList<>());
        return form;
    }
}