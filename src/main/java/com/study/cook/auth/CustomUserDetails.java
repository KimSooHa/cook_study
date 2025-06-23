package com.study.cook.auth;

import com.study.cook.domain.Member;
import com.study.cook.dto.LoginMember;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

// Security가 "/login" 요청이 오면 낚아채서 로그인을 진행시킨다.
// 로그인을 진행이 완료가 되면 session을 만들어 준다. (Security ContextHolder)
// 오브젝트 => Authentication 객체
// Authentication 타입의 객체에 저장할 수 있는 유일한 타입
// User 타입의 오브젝트 => UserDetails 타입 객체

// Security Session => Authentication => UserDetails

@RequiredArgsConstructor
@Getter
public class CustomUserDetails implements UserDetails, Serializable {

    //    private final Member member;
    private final LoginMember member;

    @Override
    public String getPassword() {
        return member.getPwd();
    }

    @Override
    public String getUsername() {
        return member.getLoginId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        return authorities;
    }
}