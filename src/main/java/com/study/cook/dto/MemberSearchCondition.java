package com.study.cook.dto;

import lombok.Data;

@Data
public class MemberSearchCondition {
    // 로그인 아이디, 비밀번호

    private String loginId;
    private String pwd;
}
