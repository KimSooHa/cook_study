package com.study.cook.interceptor;

import com.study.cook.SessionConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {

    @Override   // 로그인 여부 체크
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String requestURI = request.getRequestURI();    // uri 가져오기

        log.info("인증 체크 인터셉터 실행 {}", requestURI);

        HttpSession session = request.getSession(false);

        // 세션이 없다면
        if (session == null || session.getAttribute(SessionConst.LOGIN_MEMBER) == null) {

            log.info("미인증 사용자 요청");

            //로그인으로 redirect
            response.sendRedirect("/login?redirectURL="+requestURI);
            return false;   // 미인증 사용자는 다음으로 진행하지 x
        }

        return true;

    }

}
