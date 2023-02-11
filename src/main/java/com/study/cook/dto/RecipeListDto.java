package com.study.cook.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.study.cook.domain.Comment;
import com.study.cook.domain.RecipeField;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class RecipeListDto {

    private Long recipeId;
    private String title;
    private String img;


    @QueryProjection
    public RecipeListDto(Long recipeId, String title, String img) {
        this.recipeId = recipeId;
        this.title = title;
        this.img = img;
    }
}
