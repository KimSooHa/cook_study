package com.study.cook.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ClubListDto {

    private Long clubId;
    private String name;
    private String memberLoginId;


    @QueryProjection

    public ClubListDto(Long clubId, String name, String memberLoginId) {
        this.clubId = clubId;
        this.name = name;
        this.memberLoginId = memberLoginId;
    }
}
