package com.study.cook.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.study.cook.domain.Photo;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RecipeDto {

    private String title;
    private String introduction;
    private Photo photo;
    private String ingredients;
    private int cookingTime;
    private int servings;
    private Long heartCount;
    private String categoryName;


    @QueryProjection
    public RecipeDto(String title, String introduction, Photo photo, String ingredients, int cookingTime, int servings, String categoryName) {
        this.title = title;
        this.introduction = introduction;
        this.photo = photo;
        this.ingredients = ingredients;
        this.cookingTime = cookingTime;
        this.servings = servings;
        this.categoryName = categoryName;
    }
}
