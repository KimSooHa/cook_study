package com.study.cook.controller;

import com.study.cook.domain.Category;
import com.study.cook.domain.Photo;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class RecipeForm {

    @NotBlank(message = "제목을 작성해주세요.")
    @Size(max = 20, message = "제목은 최대 20자까지 가능합니다.")
    private String title;
    @Size(max = 200, message = "최대 200자까지 가능합니다.")
    private String introduction;
//    @NotNull(message = "이미지를 선택해주세요.")
    private MultipartFile imageFile;

    @NotBlank(message = "레시피에 필요한 재료를 작성해주세요.")
    @Size(max = 200, message = "최대 200자까지 가능합니다.")
    private String ingredients; // 재료
    @NotNull(message = "조리시간을 작성해주세요.")
    private int cookingTime;    // 요리시간
    @NotNull(message = "몇인분 음식인지 작성해주세요.")
    private int servings;   // 인분
    private Category category;

//    @Valid
    private List<RecipeFieldForm> fieldForms = new ArrayList<>();

}
