package com.study.cook.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SignupResponseDto {

    private Long memberId;
    private String email;
    private String name;
    private LocalDateTime regDate;

}
