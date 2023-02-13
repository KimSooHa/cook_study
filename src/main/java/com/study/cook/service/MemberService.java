package com.study.cook.service;

import com.study.cook.domain.Member;
import com.study.cook.dto.MemberLoginIdSearchCondition;
import com.study.cook.dto.MemberPwdSearchCondition;
import com.study.cook.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

//    @Autowired
//    public MemberService(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }

    /**
     * 회원 가입
     */
    @Transactional  // 변경해야 하기 때문에 읽기, 쓰기가 가능해야 함
    public Long join(Member member) {
        validateDuplicateMember(member);    // 중복 회원 검증
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        Optional<Member> findMember = memberRepository.findByLoginId(member.getLoginId());   // 로그인 아이디로 회원 찾기
//        Optional<Member> findMember = Optional.empty();

        // 해당 아이디의 회원이 있으면
        if (!findMember.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    // 회원 전체 조회
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    // 회원 조회
    public Member findOneById(Long memberId) {
        return memberRepository.findById(memberId).orElse(null);
    }

    public Member findOne(MemberLoginIdSearchCondition condition) {
        return memberRepository.findByEmailAndPhoneNum(condition.getEmail(), condition.getPhoneNum());
    }

    public Member findOne(MemberPwdSearchCondition condition) {
        return memberRepository.findByLoginIdAndEmail(condition.getLoginId(), condition.getEmail());
    }

    @Transactional
    public void update(Long id, String name, String loginId, String pwd, String email, String phoneNum) {
        Member member = memberRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("no such data"));
        member.setName(name);
        member.setLoginId(loginId);
        member.setPwd(pwd);
        member.setEmail(email);
        member.setPhoneNum(phoneNum);
    }

    @Transactional
    public void delete(Member member) {
        memberRepository.delete(member);
    }
}
