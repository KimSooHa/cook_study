package com.study.cook.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class ScheduleDto {
    private Long scheduleId;
    private String startTime;
    private String endTime;

    @QueryProjection
    public ScheduleDto(Long scheduleId, String startTime, String endTime) {
        this.scheduleId = scheduleId;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
