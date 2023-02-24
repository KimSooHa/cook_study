package com.study.cook.controller;

import com.study.cook.domain.Photo;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class RecipeFieldForm {

    private MultipartFile imageFile;
    @NotBlank(message = "조리과정 내용을 작성해주세요.")
    @Size(max = 200, message = "최대 200자까지 가능합니다.")
    private String content;

}
