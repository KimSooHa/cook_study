package com.study.cook.controller;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
public class CommentForm {

    @NotBlank(message = "댓글을 입력해주세요.")
    private String content;
    private Long recipeId;
}
