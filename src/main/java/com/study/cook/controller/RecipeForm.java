package com.study.cook.controller;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class RecipeForm {

    private String introduction;
    private String img;
    private String ingredients; // 재료
    private int maxCount;
    private int price;
}
