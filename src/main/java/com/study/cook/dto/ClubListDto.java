package com.study.cook.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ClubListDto {

    private Long clubId;
    private String name;


    @QueryProjection

    public ClubListDto(Long clubId, String name) {
        this.clubId = clubId;
        this.name = name;
    }
}
