package com.study.cook.controller;

import com.study.cook.validation.FieldMatch;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@FieldMatch.List({
        @FieldMatch(first = "newPwd", second = "newPwdConfirm", message = "비밀번호가 일치하지 않습니다.")
})
public class MemberUpdateForm {

    @NotBlank(message = "이름을 작성해주세요.")
    @Pattern(regexp = "^[가-힣a-zA-Z]*$")
    private String name;

    @NotBlank(message = "아이디를 작성해주세요.")
    @Pattern(regexp = "^[a-zA-Z]{1}[a-zA-Z0-9_-]{4,11}$", message = "아이디 형식에 맞지 않습니다.")
    @Size(max = 10, message = "아이디는 최대 10자까지 가능합니다.")
    private String loginId;

//    @NotBlank(message = "현재 비밀번호를 작성해주세요.")
//    @Pattern(regexp = "^.*(?=^.{8,16}$)(?=.*\\d)(?=.*[a-zA-Z])(?=.*[!@#$%^&+*=]).*$", message = "비밀번호에 영문, 숫자, 특수기호가 포함되어야 합니다.")
//    @Size(min = 8, max = 16, message = "비밀번호는 최소 8자 이상, 최대 16자 이하여야 합니다.")
    private String currentPwd;

//    @NotBlank(message = "새 비밀번호를 작성해주세요.")
    @Pattern(regexp = "^.*(?=^.{8,16}$)(?=.*\\d)(?=.*[a-zA-Z])(?=.*[!@#$%^&+*=]).*$", message = "비밀번호에 영문, 숫자, 특수기호가 포함되어야 합니다.")
    @Size(min = 8, max = 16, message = "비밀번호는 최소 8자 이상, 최대 16자 이하여야 합니다.")
    private String newPwd;

//    @NotBlank(message = "새 비밀번호를 다시 한번 작성해주세요.")
//    @Pattern(regexp = "^.*(?=^.{8,16}$)(?=.*\\d)(?=.*[a-zA-Z])(?=.*[!@#$%^&+*=]).*$", message = "비밀번호에 영문, 숫자, 특수기호가 포함되어야 합니다.")
    @Size(min = 8, max = 16, message = "비밀번호는 최소 8자 이상, 최대 16자 이하여야 합니다.")
    private String newPwdConfirm;

    @NotBlank(message = "이메일을 작성해주세요.")
    @Email(message = "이메일 형식에 맞지 않습니다.")
    @Size(max = 320, message = "이메일은 최대 320자 이하여야 합니다.")
    private String email;

    @NotBlank(message = "휴대폰 번호를 작성해주세요.")
    @Pattern(regexp = "^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$", message = "휴대폰 번호의 형식에 맞지 않습니다. '-'를 포함해서 작성해주세요.")
    private String phoneNum;

}
