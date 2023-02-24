package com.study.cook.api;

import com.study.cook.controller.ScheduleForm;
import com.study.cook.util.DateParser;
import com.study.cook.domain.CookingRoom;
import com.study.cook.domain.Reservation;
import com.study.cook.domain.Schedule;
import com.study.cook.dto.ScheduleDto;
import com.study.cook.service.CookingRoomService;
import com.study.cook.service.ReservationService;
import com.study.cook.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ScheduleApiController {

    private final ReservationService reservationService;
    private final CookingRoomService cookingRoomService;
    private final ScheduleService scheduleService;
    private final DateParser dateParser;


    @GetMapping("/reserved-time")
    public Set<Long> findReservedSchedule(@RequestParam("date") String date,
                                          @RequestParam("cookingRoomId") Long cookingRoomId) {

        log.info("date={}", date);
        LocalDate localDate = dateParser.stringToDate(date);

        CookingRoom cookingRoom = cookingRoomService.findOneById(cookingRoomId);
        List<ScheduleForm> schedules = new ArrayList<>();

        List<Reservation> reservations = reservationService.findByCookingRoomIdAndDate(cookingRoom.getId(), date).orElseThrow();
//            List<ScheduleDto> scheduleDtos = new ArrayList<>();
        log.info("reservations={}", reservations.size());

        List<CookingRoom> cookingRooms = cookingRoomService.findList();
        List<Schedule> scheduleList = scheduleService.findListByCookingRoom(cookingRoom);

        if (!reservations.isEmpty()) {
            Set<Long> reservedIdList = new HashSet<>();

            for (Reservation reservation : reservations) {
                for (Schedule schedule : scheduleList) {
                    LocalTime time = dateParser.parseToTime(reservation.getStartDateTime());
                    Schedule reservedSchedule = scheduleService.findListByCookingRoomAndStartTime(cookingRoom, time);

                    Long id = reservedSchedule.getId();
//                    if(scheduleList.contains(reservedSchedule))
//                        reservedIdList.add(id);
                    if (schedule.getId() == id)
                        reservedIdList.add(id);

                }
            }
            log.info("reservedIdList={}", reservedIdList);
            return reservedIdList;
        }


        return Collections.emptySet();

    }

}
