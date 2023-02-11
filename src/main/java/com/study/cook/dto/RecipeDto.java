package com.study.cook.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.study.cook.domain.Comment;
import com.study.cook.domain.RecipeField;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class RecipeDto {

    private String title;
    private String introduction;
    private String img;
    private String ingredients;
    private int cookingTime;
    private int servings;
    private int heartCount;
    private String categoryName;


    @QueryProjection
    public RecipeDto(String title, String introduction, String img, String ingredients, int cookingTime, int servings, int heartCount, String categoryName) {
        this.title = title;
        this.introduction = introduction;
        this.img = img;
        this.ingredients = ingredients;
        this.cookingTime = cookingTime;
        this.servings = servings;
        this.heartCount = heartCount;
        this.categoryName = categoryName;
    }
}
