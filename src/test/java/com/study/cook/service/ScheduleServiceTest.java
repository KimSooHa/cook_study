package com.study.cook.service;

import com.study.cook.domain.CookingRoom;
import com.study.cook.domain.Schedule;
import com.study.cook.repository.CookingRoomRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Slf4j
class ScheduleServiceTest {

    @Autowired
    ScheduleService scheduleService;

    @SpyBean
    CookingRoomRepository cookingRoomRepository;

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
    @DisplayName("일정 생성 및 전체 요리실과 매핑")
    void createAndMatchAll() {
        // given
        LocalTime startTime = LocalTime.of(19, 0);
        LocalTime endTime = LocalTime.of(20, 0);

        // when
        Long countBySchedule = scheduleService.createAndMatchAll(startTime, endTime);
        Mockito.when(cookingRoomRepository.count()).thenReturn(5L);

        // then
        assertThat(countBySchedule).isEqualTo(cookingRoomRepository.count());
    }

    @Test
    @DisplayName("일정 목록 조회")
    void findList() {
        // given
        Mockito.when(cookingRoomRepository.count()).thenReturn(5L);

        // when
        List<Schedule> list = scheduleService.findList();

        // then
        assertThat(list.size()).isEqualTo(8 * cookingRoomRepository.count());
    }

    @Test
    @DisplayName("요리실로 일정 목록 조회")
    void findListByCookingRoom() {
        // given
        CookingRoom cookingRoom = cookingRoomRepository.findByRoomNum(101).orElseThrow(() -> new IllegalArgumentException("no such data"));

        // when
        List<Schedule> list = scheduleService.findListByCookingRoom(cookingRoom);

        // then
        assertThat(list.size()).isEqualTo(8);
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