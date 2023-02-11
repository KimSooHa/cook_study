package com.study.cook.service;

import com.study.cook.domain.Member;
import com.study.cook.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final MemberRepository memberRepository;

    // null이면 로그인 실패
    public Member login(String loginId, String pwd) {
        return memberRepository.findByLoginId(loginId)
                .stream().filter(m -> m.getPwd().equals(pwd))
                .findAny().orElse(null);
    }

}
