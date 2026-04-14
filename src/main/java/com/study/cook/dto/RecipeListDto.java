package com.study.cook.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RecipeListDto {

    private Long recipeId;
    private String title;
    private String storeFileName;


    @QueryProjection
    public RecipeListDto(Long recipeId, String title, String storeFileName) {
        this.recipeId = recipeId;
        this.title = title;
        this.storeFileName = storeFileName;
    }
}
