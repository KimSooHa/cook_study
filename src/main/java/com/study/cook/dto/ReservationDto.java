package com.study.cook.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class ReservationDto {

    private Long reservationId;
    private String date;
    private String startTime;
    private String endTime;

    private int cookingRoomNum;

    @QueryProjection
    public ReservationDto(Long reservationId, String date, String startTime, String endTime, int cookingRoomNum) {
        this.reservationId = reservationId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.cookingRoomNum = cookingRoomNum;
    }
}
