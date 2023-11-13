package com.study.cook.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class SecurityLoginCheckInterceptor implements HandlerInterceptor {

    @Override   // 로그인 여부 체크
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String requestURI = request.getRequestURI();    // uri 가져오기

        log.info("인증 체크 인터셉터 실행 {}", requestURI);

        // 사용자가 인증되었고, "USER" 또는 "ROLE_USER" 권한을 가지고 있는 경우
        if (request.isUserInRole("USER") || request.isUserInRole("ROLE_USER"))
            return true; // 계속 진행

            // 사용자가 로그인하지 않은 경우 또는 권한이 없는 경우
            log.info("미인증 사용자 요청");
            response.sendRedirect("/loginForm?redirectURL="+requestURI); // 로그인 페이지로 이동
            return false; // 페이지 접근 거부
    }

}
