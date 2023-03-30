package com.study.cook.service;

import com.study.cook.controller.MemberForm;
import com.study.cook.domain.Member;
import com.study.cook.dto.MemberLoginIdSearchCondition;
import com.study.cook.dto.MemberPwdSearchCondition;
import com.study.cook.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    /**
     * 회원 가입
     */
    @Transactional  // 변경해야 하기 때문에 읽기, 쓰기가 가능해야 함
    public Long join(MemberForm form) {
        Member member = new Member(form.getName(), form.getLoginId(), form.getPwd(), form.getEmail(), form.getPhoneNum());
        validateDuplicateMember(member);    // 중복 회원 검증
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        Optional<Member> findMember = memberRepository.findByLoginId(member.getLoginId());   // 로그인 아이디로 회원 찾기

        findMember.ifPresent((m) -> {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        });
        Long emailCnt = countByEmail(member.getEmail());
        // 해당 아이디의 회원이 있으면
        if (emailCnt != 0) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }
    public Long countByLoginId(String loginId) {
        return memberRepository.countByLoginId(loginId);
    }

    public Long countByEmail(String email) {
        return memberRepository.countByEmail(email);
    }

    public Long countByPhoneNum(String phoneNum) {
        return memberRepository.countByPhoneNum(phoneNum);
    }

    // 회원 조회
    public Member findOneById(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("no such data"));
    }

    public Member findOneByEmail(String email) {
        return memberRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("no such data"));
    }


    public Member findOne(MemberLoginIdSearchCondition condition) {
        return memberRepository.findByEmailAndPhoneNum(condition.getEmail(), condition.getPhoneNum());
    }

    public Member findOne(MemberPwdSearchCondition condition) {
        return memberRepository.findByLoginIdAndEmail(condition.getLoginId(), condition.getEmail());
    }

    @Transactional
    public void update(Long id, MemberForm form) {
        Member member = memberRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("no such data"));
        member.setName(form.getName());
        member.setLoginId(form.getLoginId());
        member.setPwd(form.getPwd());
        member.setEmail(form.getEmail());
        member.setPhoneNum(form.getPhoneNum());
    }

    @Transactional
    public void delete(Long memberId) {
        Member member = findOneById(memberId);
        memberRepository.delete(member);
    }
}
