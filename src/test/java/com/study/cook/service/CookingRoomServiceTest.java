package com.study.cook.service;

import com.study.cook.domain.CookingRoom;
import com.study.cook.domain.Schedule;
import com.study.cook.repository.ScheduleRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Slf4j
class CookingRoomServiceTest {

    @Autowired
    CookingRoomService cookingRoomService;

    @Autowired
    ScheduleRepository scheduleRepository;

    @Test
    @DisplayName("요리실 등록")
    void create() {
        // given
        CookingRoom cookingRoom = new CookingRoom(10, 101);
        List<Schedule> schedules = scheduleRepository.findAll();

        // when
        Long cookingRoomId = cookingRoomService.create(cookingRoom, schedules);

        // then
        assertThat(cookingRoomId).isEqualTo(cookingRoom.getId());

    }

    @Test
    @DisplayName("요리실 목록 조회")
    void findList() {
        // when
        List<CookingRoom> list = cookingRoomService.findList();

        // then
        assertThat(list.size()).isEqualTo(5);
    }

    @Test
    void findOneById() {
    }

    @Test
    void findOneByRoomNum() {
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }
}