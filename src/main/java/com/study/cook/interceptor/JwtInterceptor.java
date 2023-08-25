package com.study.cook.interceptor;

import com.study.cook.exception.JwtExceptionCode;
import com.study.cook.util.JwtAuthenticationToken;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class JwtInterceptor implements HandlerInterceptor {

//    @Override   // 로그인 여부 체크
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        log.info(">>> interceptor.preHandle 호출");
//        HandlerMethod handlerMethod = (HandlerMethod) handler;
//        Method method = handlerMethod.getMethod();
//
//        if(method.isAnnotationPresent(JwtRequired.class) ||
//        handlerMethod.getBeanType().isAnnotationPresent(JwtRequired.class)){
//            String token = request.getHeader("Authorization");
//
//            // JWT 토큰 검증 로직을 수행하고, 토큰이 유효하지 않다면 예외를 발생시킴
//            if(token == null) {
//                log.info("미인증 사용자 요청");
//                return false;   // 미인증 사용자는 다음으로 진행하지 x
//            }
//        }
//
//        return true;
//    }

    private AuthenticationManager authenticationManager;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();    // uri 가져오기

        log.info("인증 체크 인터셉터 실행 {}", requestURI);

        String token = getToken(request);
        if (StringUtils.hasText(token)) {
            try {
                getAuthentication(token);
            } catch (NullPointerException | IllegalStateException e) {
                handleException(request, JwtExceptionCode.NOT_FOUND_TOKEN.getCode(), "Not found Token // token : " + token);
            } catch (SecurityException | MalformedJwtException e) {
                handleException(request, JwtExceptionCode.INVALID_TOKEN.getCode(), "Invalid Token // token : " + token);
            } catch (ExpiredJwtException e) {
                handleException(request, JwtExceptionCode.EXPIRED_TOKEN.getCode(), "EXPIRED Token // token : " + token);
            } catch (UnsupportedJwtException e) {
                handleException(request, JwtExceptionCode.UNSUPPORTED_TOKEN.getCode(), "Unsupported Token // token : " + token);
            } catch (Exception e) {
                logErrorWithStackTrace("JwtInterceptor - preHandle() 오류 발생", token, e);
                handleException(request, "throw new exception", "Exception occurred");
            }
        } else {
            // Handle case when token is missing
            log.info("미인증 사용자 요청");

            //로그인으로 redirect
            response.sendRedirect("/login?redirectURL="+requestURI);
            return false;
        }

        return true;
    }

    private void getAuthentication(String token) {
        JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(token);
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authenticate);
    }

    private String getToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer")) {
            String[] arr = authorization.split(" ");
            return arr[1];
        }
        return null;
    }

    private void handleException(HttpServletRequest request, String errorCode, String errorMessage) throws ServletException {
        request.setAttribute("exception", errorCode);
        log.error("Set Request Exception Code : {}", errorCode);
        log.error(errorMessage);
        throw new BadCredentialsException("throw new exception");
    }

    private void logErrorWithStackTrace(String message, String token, Exception e) {
        log.error("====================================================");
        log.error(message);
        log.error("token : {}", token);
        log.error("Exception Message : {}", e.getMessage());
        log.error("Exception StackTrace : {");
        e.printStackTrace();
        log.error("}");
        log.error("====================================================");
    }

}
