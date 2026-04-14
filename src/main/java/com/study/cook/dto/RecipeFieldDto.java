package com.study.cook.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.study.cook.domain.Photo;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RecipeFieldDto {

    private Photo photo;
    private String content;

    @QueryProjection
    public RecipeFieldDto(Photo photo, String content) {
        this.photo = photo;
        this.content = content;
    }
}
