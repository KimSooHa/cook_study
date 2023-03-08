package com.study.cook.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.study.cook.domain.Comment;
import com.study.cook.domain.Photo;
import com.study.cook.domain.RecipeField;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class RecipeFieldDto {

    private Photo img;
    private String content;

    @QueryProjection
    public RecipeFieldDto(Photo img, String content) {
        this.img = img;
        this.content = content;
    }
}
