package com.study.cook.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Component
public class DateParser {

    // DateTime -> String
    public String getFormatTime(LocalTime time) {
        return time.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    public String getFormatTime(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
    }
    public String getFormatDateString(LocalDate date) {
        return LocalDate.of(date.getYear(), date.getMonth(), date.getDayOfMonth()).format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
    }

    public String getFormatDate(LocalDateTime dateTime) {
        return LocalDate.of(dateTime.getYear(), dateTime.getMonth(), dateTime.getDayOfMonth()).format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
    }

    // Date + Time
    public LocalDateTime parseToDateTime(LocalDate date, LocalTime time) {
        LocalDateTime dateTime = LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), time.getHour(), time.getMinute());
        return dateTime;
    }

    public LocalDate parseToDate(LocalDate date) {
        return LocalDate.of(date.getYear(), date.getMonth(), date.getDayOfMonth());
    }

    public LocalDate parseToDate(LocalDateTime dateTime) {
        return LocalDate.of(dateTime.getYear(), dateTime.getMonth(), dateTime.getDayOfMonth());
    }

    public LocalTime parseToTime(LocalDateTime dateTime) {
        return LocalTime.of(dateTime.getHour(), dateTime.getMinute());
    }

    public LocalDate stringToDate(String date) {
        return LocalDate.parse(date);
    }
}
