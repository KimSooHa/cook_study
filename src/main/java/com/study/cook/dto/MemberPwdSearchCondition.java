package com.study.cook.dto;

import lombok.Data;

@Data
public class MemberPwdSearchCondition {
    // 로그인 아이디, 이메일

    private String loginId;
    private String email;
}
