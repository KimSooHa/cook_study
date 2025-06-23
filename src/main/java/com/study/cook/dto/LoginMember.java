package com.study.cook.dto;

import com.study.cook.domain.Member;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class LoginMember implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Long id;
    private final String loginId;
    private final String pwd;
    private final String name;

    public LoginMember(Member member) {
        this.id = member.getId();
        this.loginId = member.getLoginId();
        this.pwd = member.getPwd();
        this.name = member.getName();
    }
}