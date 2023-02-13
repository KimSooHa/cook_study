package com.study.cook;

import com.study.cook.argumentresolver.LoginMemberArgumentResolver;
import com.study.cook.interceptor.LoginCheckInterceptor;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new LoginCheckInterceptor())
//                .order(1)
//                .addPathPatterns("/**")
//                .excludePathPatterns("/", "/resources/static/css/**", "/login", "/logout", "/recipes", "/clubs",
//                        "/css/**", "/cooking-rooms", "/members/searchId", "/members/searchPwd", "/error");
    }

//    @Override
//    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
//        resolvers.add(new LoginMemberArgumentResolver());
//    }
}
