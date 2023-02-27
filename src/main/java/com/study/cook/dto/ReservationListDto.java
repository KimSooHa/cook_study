package com.study.cook.dto;

import com.study.cook.domain.Reservation;
import com.study.cook.util.DateParser;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ReservationListDto {

    private Long id;
    private String date;
    private String startTime;
    private String endTime;
    private int cookingRoomName;

    public static ReservationListDto toDtoList(Reservation reservation) {

        DateParser dateParser = new DateParser();

        return ReservationListDto.builder()
                    .id(reservation.getId())
                    .date(dateParser.getFormatDate(reservation.getStartDateTime()))
                    .startTime(dateParser.getFormatTime(reservation.getStartDateTime()))
                    .endTime(dateParser.getFormatTime(reservation.getEndDateTime()))
                    .cookingRoomName(reservation.getCookingRoom().getRoomNum())
                    .build();
    }
}
