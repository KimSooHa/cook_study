package com.study.cook.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.study.cook.domain.Photo;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RecipeListDto {

    private Long recipeId;
    private String title;
    private Photo img;


    @QueryProjection
    public RecipeListDto(Long recipeId, String title, Photo img) {
        this.recipeId = recipeId;
        this.title = title;
        this.img = img;
    }
}
