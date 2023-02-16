package com.study.cook.controller;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
public class LoginForm {

    @NotEmpty(message = "아이디를 작성해주세요.")
    @NotBlank(message = "공백은 입력할 수 없습니다.")
    private String loginId;
    @NotEmpty(message = "비밀번호를 작성해주세요.")
    @NotBlank(message = "공백은 입력할 수 없습니다.")
    private String pwd;
}
