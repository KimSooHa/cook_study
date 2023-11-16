package com.study.cook.config;

import com.study.cook.argumentresolver.SecurityLoginMemberArgumentResolver;
import com.study.cook.interceptor.SecurityLoginCheckInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new LoginCheckInterceptor())
//                .order(1)
//                .addPathPatterns("/**")
//                .excludePathPatterns("/", "/script/**", "/login", "/logout", "/valid-email", "/valid-loginId", "/valid-phoneNum", "/recipes/images/**",
//                        "/reserved-time", "/cooking-rooms", "/css/**", "/members", "/members/searchId", "/members/searchPwd", "/*.ico", "/error", "/image/**");
//
        registry.addInterceptor(new SecurityLoginCheckInterceptor())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/", "/script/**", "/loginForm", "/logout", "/valid-email", "/valid-loginId", "/valid-phoneNum", "/recipes/images/**",
                        "/reserved-time", "/cooking-rooms", "/css/**", "/members", "/members/searchId", "/members/searchPwd", "/*.ico", "/error", "/image/**");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new SecurityLoginMemberArgumentResolver());
    }
}
