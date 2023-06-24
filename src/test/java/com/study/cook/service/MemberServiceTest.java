package com.study.cook.service;

import com.study.cook.controller.MemberForm;
import com.study.cook.domain.Member;
import com.study.cook.dto.MemberLoginIdSearchCondition;
import com.study.cook.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Slf4j
class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager em;

    @BeforeEach
    @Transactional
    public void testSave() {
        for (int i = 0; i < 20; i++) {
            memberRepository.save(new Member("member" + i, "member" + i, "member1234*", "member" + i + "@email.com", "010-1234-1234"));
        }
    }

//    @Test
//    @Rollback(value = false)    // 테스트케이스에서는 끝날때 롤백이 기본인데, db를 보기 위해서 커밋하기 위해 false로 설정가능
    public void testMember() throws Exception {
        // given
        MemberLoginIdSearchCondition condition = new MemberLoginIdSearchCondition();
        condition.setEmail("member5@email.com");
        condition.setPhoneNum("010-1234-1234");
        // when
        Member findMember = memberService.findOne(condition);

        // then
        assertThat(findMember.getLoginId()).isEqualTo("member5");
//        log.info("memberLoginId = {}", findMember.getLoginId());
    }

    @Test
    @Transactional
    @DisplayName("회원가입")
    public void signupTest() {
        // given
        MemberForm form = new MemberForm();
        form.setName("member");
        form.setLoginId("member1010");
        form.setPwd("member123**");
        form.setEmail("membertest@naver.com");
        form.setPhoneNum("010-0000-1111");

        // when
        Long joinedId = memberService.join(form);

        // then
        assertThat(memberService.findOneById(joinedId)).isEqualTo(memberService.findOneByEmail(form.getEmail()));
    }

}