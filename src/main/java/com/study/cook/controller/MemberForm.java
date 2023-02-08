package com.study.cook.controller;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MemberForm {

    private String name;
    private String nickname;
    private String loginId;
    private String pwd;
    private String email;
    private String phoneNum;

}
