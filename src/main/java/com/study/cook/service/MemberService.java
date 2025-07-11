package com.study.cook.service;

import com.study.cook.controller.MemberForm;
import com.study.cook.controller.MemberUpdateForm;
import com.study.cook.domain.Member;
import com.study.cook.dto.MemberLoginIdSearchCondition;
import com.study.cook.dto.MemberPwdSearchCondition;
import com.study.cook.dto.MemberSearchCondition;
import com.study.cook.exception.CheckMatchPwdException;
import com.study.cook.exception.CheckNewPwdException;
import com.study.cook.exception.FindMemberException;
import com.study.cook.exception.LoginFailException;
import com.study.cook.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원 가입
     */
    @Transactional  // 변경해야 하기 때문에 읽기, 쓰기가 가능해야 함
    public Long join(MemberForm form) {
        Member member = new Member(form.getName(), form.getLoginId(), passwordEncoder.encode(form.getPwd()), form.getEmail(), form.getPhoneNum());
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
    public Long countByLoginId(String loginId, Long memberId) {
        if(memberId != null) {
            Long cnt = memberRepository.countByLoginIdAndId(loginId, memberId);
            if(cnt > 0)
                return (long) 0;
        }
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
        return memberRepository.findById(memberId).orElseThrow(() -> new FindMemberException("no such data"));
    }

    public Member findOneByLoginIdAndPwd(MemberSearchCondition condition) {
        return memberRepository.findByLoginId(condition.getLoginId())
                .stream().filter(m -> condition.getPwd().equals(m.getPwd()))
                .findAny().orElseThrow(() -> new LoginFailException("아이디 또는 비밀번호가 맞지 않습니다."));
    }

    public Member findOneByEmail(String email) {
        return memberRepository.findByEmail(email).orElseThrow(() -> new FindMemberException("no such data"));
    }


    public Member findOne(MemberLoginIdSearchCondition condition) {
        return memberRepository.findByEmailAndPhoneNum(condition.getEmail(), condition.getPhoneNum());
    }

    public Member findOne(MemberPwdSearchCondition condition) {
        return memberRepository.findByLoginIdAndEmail(condition.getLoginId(), condition.getEmail());
    }

    @Transactional
    public void update(Long id, MemberUpdateForm form) {
        Member member = memberRepository.findById(id).orElseThrow(() -> new FindMemberException("수정 실패: 해당 회원을 찾을 수 없습니다."));
        member.setName(form.getName());
        member.setLoginId(form.getLoginId());

        if(!form.getCurrentPwd().isEmpty()) {
            if(!passwordEncoder.matches(form.getCurrentPwd(), member.getPwd()))
                throw new CheckMatchPwdException("기존 비밀번호와 일치하지 않습니다");

            if(!form.getNewPwd().equals(form.getNewPwdConfirm()))
            throw new CheckNewPwdException("비밀번호와 비밀번호 확인이 일치하지 않습니다.");

            member.setPwd(passwordEncoder.encode(form.getNewPwd()));
        }

        member.setEmail(form.getEmail());
        member.setPhoneNum(form.getPhoneNum());
    }

    @Transactional
    public void delete(Long memberId) {
        Member member = findOneById(memberId);
        memberRepository.delete(member);
    }
}
