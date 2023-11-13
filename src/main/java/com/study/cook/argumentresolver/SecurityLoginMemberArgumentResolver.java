package com.study.cook.argumentresolver;


import com.study.cook.auth.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * ArgumentResolver for Security Session
 */
@Slf4j
public class SecurityLoginMemberArgumentResolver implements HandlerMethodArgumentResolver {


    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        log.info("supportsParameter 실행");
        return parameter.getParameterType().equals(CustomUserDetails.class);
    }

    // 컨트롤러 호출 직전에 호출(필요한 파라미터 정보 생성)
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory)
            throws Exception {

        log.info("resolveArgument 실행");

        /**
         * spring security - session formLogin
         */
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("principal = {}", authentication);
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            log.info("userDetails = {}", userDetails);
            return userDetails;
        }
        return null;
    }
}
