package com.study.cook.controller;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ReservationForm {

    private Long id;
    @NotNull
    private String date;
    @NotNull
    private Long cookingRoomId;
    @NotNull
    private List<Long> scheduleIds = new ArrayList<>();
}
