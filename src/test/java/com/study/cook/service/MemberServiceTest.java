package com.study.cook.service;

import com.study.cook.domain.Member;
import com.study.cook.dto.MemberLoginIdSearchCondition;
import com.study.cook.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;

@SpringBootTest
//@Slf4j
class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager em;

//    @BeforeEach
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
        Assertions.assertThat(findMember.getLoginId()).isEqualTo("member5");
//        log.info("memberLoginId = {}", findMember.getLoginId());
    }



}