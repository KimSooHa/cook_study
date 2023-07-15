package com.study.cook.service;

import com.study.cook.domain.CookingRoom;
import com.study.cook.domain.Schedule;
import com.study.cook.repository.ScheduleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
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
    @DisplayName("아이디로 조회")
    void findOneById() {
        // given
        CookingRoom cookingRoom = new CookingRoom(10, 101);
        List<Schedule> schedules = scheduleRepository.findAll();
        Long cookingRoomId = cookingRoomService.create(cookingRoom, schedules);

        // when
        CookingRoom findCookingRoom = cookingRoomService.findOneById(cookingRoomId);

        // then
        assertThat(findCookingRoom.getId()).isEqualTo(cookingRoom.getId());
    }

    @Test
    @DisplayName("요리실 번호로 조회")
    void findOneByRoomNum() {
        // given
        int roomNum = 101;

        // when
        CookingRoom cookingRoom = cookingRoomService.findOneByRoomNum(roomNum);

        // then
        assertThat(cookingRoom).isNotNull();
    }

    @Test
    @DisplayName("요리실 수정")
    void update() {
        // given
        CookingRoom cookingRoom = new CookingRoom(10, 101);
        int maxCount = cookingRoom.getMaxCount();
        List<Schedule> schedules = scheduleRepository.findAll();
        Long cookingRoomId = cookingRoomService.create(cookingRoom, schedules);

        // when
        cookingRoomService.update(cookingRoomId, 15, cookingRoom.getRoomNum());

        // then
        assertThat(cookingRoom.getMaxCount()).isNotEqualTo(maxCount);
    }

    @Test
    @DisplayName("요리실 삭제")
    void delete() {
        // given
        CookingRoom cookingRoom = new CookingRoom(10, 101);
        List<Schedule> schedules = scheduleRepository.findAll();
        Long cookingRoomId = cookingRoomService.create(cookingRoom, schedules);

        // when
        cookingRoomService.delete(cookingRoom);

        // then
        assertThatThrownBy(() -> cookingRoomService.findOneById(cookingRoomId)).isInstanceOf(IllegalArgumentException.class);
    }
}