package com.study.cook.dto;

import lombok.Data;

import javax.persistence.Enumerated;

@Data
public class RecipeSearchCondition {
    // 제목, 카테고리명, 리뷰개수순, 좋아요 갯수순, 최신순

    private String title;
    private String categoryName;
    @Enumerated
    private RecipeSortStatus sortStatus;
}
