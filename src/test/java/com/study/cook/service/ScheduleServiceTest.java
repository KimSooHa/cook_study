package com.study.cook.service;

import com.study.cook.domain.CookingRoom;
import com.study.cook.domain.Schedule;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Slf4j
class ScheduleServiceTest {

    @Autowired
    ScheduleService scheduleService;

    @Test
    @DisplayName("일정 생성")
    void create() {
        // given
        LocalTime startTime = LocalTime.of(10, 0);
        LocalTime endTime = LocalTime.of(11, 0);
        Schedule schedule = new Schedule(startTime, endTime);

        CookingRoom cookingRoom = new CookingRoom(10, 101);

        // when
        Long scheduleId = scheduleService.create(schedule, cookingRoom);

        // then
        assertThat(scheduleId).isEqualTo(schedule.getId());
        assertThat(schedule.getCookingRoom().getId()).isEqualTo(cookingRoom.getId());
    }

    @Test
    void createAndMatchAll() {
    }

    @Test
    void findList() {
    }

    @Test
    void findListByCookingRoom() {
    }

    @Test
    void findListByCookingRoomAndStartTime() {
    }

    @Test
    void findOneById() {
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }

    @Test
    void deleteAllByTime() {
    }
}