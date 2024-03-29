package com.study.cook.controller;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReservationListForm {

    private Long id;
    private int cookingRoomName;
    private String date;
    private String startTime;
    private String endTime;
}
