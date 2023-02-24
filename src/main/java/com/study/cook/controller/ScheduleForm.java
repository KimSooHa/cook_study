package com.study.cook.controller;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class ScheduleForm {

    private Long scheduleId;
    private String startDate;
    private String endDate;
}
