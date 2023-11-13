package com.study.cook.controller;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
public class LoginForm {

    @NotEmpty(message = "아이디를 작성해주세요.")
    @NotBlank(message = "공백은 입력할 수 없습니다.")
    @Pattern(regexp = "^[a-zA-Z]{1}[a-zA-Z0-9_-]{4,11}$", message = "아이디 형식에 맞지 않습니다.")
    private String loginId;
    @NotEmpty(message = "비밀번호를 작성해주세요.")
    @NotBlank(message = "공백은 입력할 수 없습니다.")
    @Pattern(regexp = "^.*(?=^.{8,16}$)(?=.*\\d)(?=.*[a-zA-Z])(?=.*[!@#$%^&+*=]).*$", message = "비밀번호에 영문, 숫자, 특수기호가 포함되어야 합니다.")
    private String pwd;
}
