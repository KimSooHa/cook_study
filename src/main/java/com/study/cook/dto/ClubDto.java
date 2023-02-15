package com.study.cook.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.study.cook.domain.ClubStatus;
import lombok.Data;

@Data
public class ClubDto {

    private String name;
    private String introduction;
    private ClubStatus status;
    private int maxCount;

    private int restCount;
    private int price;
    private String ingredients;

    private Long reservationId;
    private String date;
    private String startTime;
    private String endTime;
    private int cookingRoomNum;

    @QueryProjection

    public ClubDto(String name, String introduction, ClubStatus status, int maxCount, int restCount, int price, String ingredients, Long reservationId, String date, String startTime, String endTime, int cookingRoomNum) {
        this.name = name;
        this.introduction = introduction;
        this.status = status;
        this.maxCount = maxCount;
        this.restCount = restCount;
        this.price = price;
        this.ingredients = ingredients;
        this.reservationId = reservationId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.cookingRoomNum = cookingRoomNum;
    }
}
