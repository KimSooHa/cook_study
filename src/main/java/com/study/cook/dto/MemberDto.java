package com.study.cook.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemberDto {

    private String name;
    private String loginId;
    private String pwd;
    private String phoneNum;

    @QueryProjection
    public MemberDto(String name, String loginId, String pwd, String phoneNum) {
        this.name = name;
        this.loginId = loginId;
        this.pwd = pwd;
        this.phoneNum = phoneNum;
    }
}
