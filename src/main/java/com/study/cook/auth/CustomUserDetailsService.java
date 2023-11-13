package com.study.cook.auth;

import com.study.cook.domain.Member;
import com.study.cook.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final MemberRepository memberRepository;

	// 시큐리티 session(내부 Authentication(내부에 UserDetails 들어감))
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.info("CustomUserDetailsService : 진입");
		log.info("loginId={}", username);
		Member member = memberRepository.findByLoginId(username).orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
		// session.setAttribute("loginUser", user);
		log.info("member = {}", member);

		return new CustomUserDetails(member);
	}
}