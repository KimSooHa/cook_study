package com.study.cook.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.study.cook.domain.ClubStatus;
import com.study.cook.domain.Reservation;
import lombok.Data;

import java.util.List;

@Data
public class ClubDto {

    private String name;
    private String introduction;
    private ClubStatus status;
    private int maxCount;

    private int restCount;
    private int price;
    private String ingredients;


    @QueryProjection
    public ClubDto(String name, String introduction, ClubStatus status, int maxCount, int restCount, int price, String ingredients) {
        this.name = name;
        this.introduction = introduction;
        this.status = status;
        this.maxCount = maxCount;
        this.restCount = restCount;
        this.price = price;
        this.ingredients = ingredients;
    }
}
