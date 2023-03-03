package com.study.cook.controller;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class ClubForm {

    @NotBlank(message = "제목을 작성해주세요.")
    private String name;
    @NotBlank(message = "설명을 작성해주세요.")
    private String introduction;
    @NotNull(message = "모집 인원을 선택해주세요.")
    private int maxCount;
    @NotBlank(message = "재료를 작성해주세요.")
    private String ingredients;
    @NotNull(message = "카테고리를 선택해주세요.")
    private Long categoryId;
    private Long memberId;
    private List<Long> reservationIds;

}
