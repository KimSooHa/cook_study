package com.study.cook.dto;

import lombok.Data;

@Data
public class MemberLoginIdSearchCondition {
    // 이메일, 전화번호

    private String email;
    private String phoneNum;
}
