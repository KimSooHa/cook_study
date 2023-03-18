package com.study.cook.controller;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ReservationForm {

    private Long id;
    @NotEmpty(message = "날짜를 선택해주세요.")
    private String date;
    @NotNull(message = "요리실을 선택해주세요.")
    private Long cookingRoomId;
    @Valid
    @NotNull
    @Size(min = 1, message = "시간을 선택해주세요.")
    private List<Long> scheduleIds = new ArrayList<>();
}
