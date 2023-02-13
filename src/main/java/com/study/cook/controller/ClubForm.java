package com.study.cook.controller;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClubForm {

    private String name;
    private String introduction;
    private int maxCount;
    private String ingredients;
    private Long categoryId;
    private Long memberId;

}
