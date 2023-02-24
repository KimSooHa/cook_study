package com.study.cook.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
//@AllArgsConstructor
public class ScheduleListForm {

//    @NotEmpty
//    private String name;
    private List<ScheduleForm> scheduleForms = new ArrayList<>();
}
