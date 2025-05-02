package com.study.cook.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.study.cook.domain.Member;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class RecipeDetailDto {

    private RecipeDto recipe;
    private List<RecipeFieldDto> recipeFields;
    private Member member;

    @QueryProjection
    public RecipeDetailDto(RecipeDto recipe, List<RecipeFieldDto> recipeFields, Member member) {
        this.recipe = recipe;
        this.recipeFields = recipeFields;
        this.member = member;
    }
}
