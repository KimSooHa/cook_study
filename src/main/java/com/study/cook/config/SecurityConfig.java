package com.study.cook.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// Spring Security 설정
@Configuration
@RequiredArgsConstructor
@EnableWebSecurity // 기본적인 Web 보안 활성화
public class SecurityConfig  {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeRequests(authorizeRequests ->
                    authorizeRequests
                            .antMatchers("/", "/script/**", "/loginForm", "/logout", "/valid-email", "/valid-loginId", "/valid-phoneNum", "/recipes/images/**",
                            "/reserved-time", "/cooking-rooms", "/css/**", "/members", "/members/searchId", "/members/searchPwd", "/*.ico", "/error", "/image/**").permitAll()
                            .anyRequest().authenticated()
            )
            .formLogin()
                .usernameParameter("loginId")
                .passwordParameter("pwd")
                .loginPage("/loginForm") // 로그인 페이지의 경로 설정
                .loginProcessingUrl("/login") // 로그인 처리 url
                .failureUrl("/loginForm?error=true") // 로그인 실패 시 오류 파라미터를 전달
                .defaultSuccessUrl("/") // 로그인 성공 후 이동할 기본 페이지("/")로 설정. 만약 요청한 페이지가 있다면 로그인 후 해당 페이지로 Redirect
                .and()
            .logout()
               .permitAll().logoutSuccessUrl("/").invalidateHttpSession(true).deleteCookies("JSESSIONID")
            .and()
            .sessionManagement(sessionManagement ->
                    sessionManagement
                            .invalidSessionUrl("/login")
                            .maximumSessions(1)
//                            .maxSessionsPreventsLogin(false)
                            .expiredUrl("/login")
            );

        return http.build();
    }

    // 암호를 암호화하거나, 사용자가 입력한 암호가 기존 암호랑 일치하는지 검사할 때 이 Bean을 사용
    // 해당 메서드의 반환되는 오브젝트를 IOC로 등록해줌
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // BCrypt: 비밀번호를 안전하게 저장하기 위한 해시 함수
    }

}
