package com.study.cook.service;

import com.study.cook.domain.CookingRoom;
import com.study.cook.domain.Schedule;
import com.study.cook.repository.ScheduleRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
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
    void findList() {
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