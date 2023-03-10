package com.study.cook.controller;

import com.study.cook.domain.Category;
import com.study.cook.domain.Reservation;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@Setter
public class ClubForm {

    @NotBlank(message = "제목을 작성해주세요.")
    private String name;
    @NotBlank(message = "설명을 작성해주세요.")
    private String introduction;
    private int maxCount;
    @NotBlank(message = "재료를 작성해주세요.")
    private String ingredients;
    private Long categoryId;
    private Long memberId;
    private List<Long> reservationIds;

}
