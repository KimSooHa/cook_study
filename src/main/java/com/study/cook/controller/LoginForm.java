package com.study.cook.controller;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
public class LoginForm {

    @NotEmpty
    @NotBlank(message = "공백은 입력할 수 없습니다.")
    private String loginId;
    @NotEmpty
    @NotBlank(message = "공백은 입력할 수 없습니다.")
    private String pwd;
}
