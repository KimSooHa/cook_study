package com.study.cook.argumentresolver;


import com.study.cook.SessionConst;
import com.study.cook.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {


    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        log.info("supportsParameter 실행");

        boolean hasLoginAnnotation = parameter.hasParameterAnnotation(Login.class); // @Login이 있는지 확인
        boolean hasMemberType = Member.class.isAssignableFrom(parameter.getParameterType()); // Member와 파라미터의 객체 타입이 동일한지 확인

        return hasLoginAnnotation && hasMemberType; // true면 resolveArgument 실행
    }

    // 컨트롤러 호출 직전에 호출(필요한 파라미터 정보 생성)
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory)
            throws Exception {

        log.info("resolveArgument 실행");

        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        HttpSession session = request.getSession(false);    // 세션 가져오기

        if (session == null) {
            return null;
        }

        // 세션이 있으면 loginMember로 된 객체 반환
        return session.getAttribute(SessionConst.LOGIN_MEMBER);

    }
}
