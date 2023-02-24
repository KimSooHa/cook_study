package com.study.cook.controller;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ReservationForm {

    private Long id;
    private String date;
    private Long cookingRoomId;
    private List<Long> scheduleIds;
}
