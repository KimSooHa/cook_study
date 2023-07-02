package com.study.cook.service;

import com.study.cook.controller.MemberForm;
import com.study.cook.domain.Member;
import com.study.cook.dto.MemberLoginIdSearchCondition;
import com.study.cook.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@Slf4j
class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager em;


    @BeforeEach
    void testSave() {
        for (int i = 0; i < 10; i++) {
            Member member = new Member("testMember" + i, "test" + i, "testMember1234*", "testMember" + i + "@email.com", "010-1234-123"+i);
            memberRepository.save(member);
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
    @DisplayName("회원가입")
    void signupTest() {
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

    @Test
    @DisplayName("중복회원 예외 검사")
    void validateDuplicateMemberTest() throws IllegalStateException {
        // given
        MemberForm form = new MemberForm();
        form.setName("testMember1");
        form.setLoginId("test1");
        form.setPwd("testMember1234*");
        form.setEmail("testMember1@email.com");
        form.setPhoneNum("010-1234-1234");

        // then
        assertThatThrownBy(() -> memberService.join(form)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("로그인 아이디로 카운트")
    void countByLoginIdTest() {
        // given
        String email = "testMember1@email.com";
        Member member = memberService.findOneByEmail(email);

        // when
        String loginId = member.getLoginId();

        // then
        assertThat(memberService.countByLoginId(loginId)).isEqualTo(1);
    }

    @Test
    @DisplayName("이메일로 카운트")
    void countByEmailTest() {
        // given
        String email = "testMember1@email.com";

        // when
        Long count = memberService.countByEmail(email);

        // then
        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("휴대폰 번호로 카운트")
    void countByPhoneNumTest() {
        // given
        String email = "testMember1@email.com";
        Member member = memberService.findOneByEmail(email);

        // when
        Long count = memberService.countByPhoneNum(member.getPhoneNum());

        // then
        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("아이디로 찾기")
    void findOneByIdTest() {
        // given
        String email = "testMember1@email.com";
        Member member = memberService.findOneByEmail(email);

        // when
        Member findMember = memberService.findOneById(member.getId());

        // then
        assertThat(findMember.getEmail()).isEqualTo(email);
    }

    @Test
    @DisplayName("이메일로 찾기")
    void findOneByEmailTest() {
        // given
        String email = "testMember1@email.com";

        // when
        Member member = memberService.findOneByEmail(email);

        // then
        assertThat(member).isNotNull();
    }

    @Test
    @DisplayName("회원정보 수정")
    void updateTest() {
        // given
        String email = "testMember1@email.com";
        Member member = memberService.findOneByEmail(email);

        MemberForm form = new MemberForm();
        form.setLoginId(member.getLoginId());
        form.setName(member.getName());
        form.setPwd(member.getPwd());
        form.setPhoneNum("010-1234-5678");

        // when
        memberService.update(member.getId(), form);

        // then
        assertThat(member.getPhoneNum()).isEqualTo(form.getPhoneNum());
    }

    @Test
    @DisplayName("회원 삭제")
    void deleteTest() {
        // given
        String email = "testMember1@email.com";
        Member member = memberService.findOneByEmail(email);

        // when
        memberService.delete(member.getId());

        // then
        assertThatThrownBy(() -> memberService.findOneById(member.getId()))
                .isInstanceOf(IllegalArgumentException.class);
    }
}