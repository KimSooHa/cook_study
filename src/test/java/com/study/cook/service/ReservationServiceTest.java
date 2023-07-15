package com.study.cook.service;

import com.study.cook.controller.ReservationForm;
import com.study.cook.domain.CookingRoom;
import com.study.cook.domain.Member;
import com.study.cook.domain.Schedule;
import com.study.cook.repository.CookingRoomRepository;
import com.study.cook.repository.MemberRepository;
import com.study.cook.repository.ScheduleRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ReservationServiceTest {

    @Autowired
    ReservationService reservationService;

    @Autowired
    ScheduleRepository scheduleRepository;

    @Autowired
    CookingRoomRepository cookingRoomRepository;

    @Autowired
    MemberRepository memberRepository;

    @BeforeEach
    void testSave() {
        Member member = new Member("testMember", "test1", "testMember1234*", "testMember@email.com", "010-1234-1234");
        memberRepository.save(member);
    }

    @Test
    @DisplayName("요리실 예약")
    void create() {
        // given
        ReservationForm form = setForm();
        Member member = memberRepository.findByLoginId("test1").orElseThrow(() -> new IllegalArgumentException("해당하는 회원이 없습니다."));

        // when
        List<Long> reservations = reservationService.create(form, member);

        // then
        assertThat(reservations.size()).isEqualTo(1);
    }

    @Test
    void findList() {
    }

    @Test
    void findByCookingRoomIdAndDate() {
    }

    @Test
    void findOneById() {
    }

    @Test
    void findByDateAndCookingRoom() {
    }

    @Test
    void findByMember() {
    }

    @Test
    void findByMemberAndDateGt() {
    }

    @Test
    void findByClub() {
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }

    private ReservationForm setForm() {
        ReservationForm form = new ReservationForm();
        LocalDateTime now = LocalDateTime.now();
        LocalDate date = LocalDate.of(now.getYear(), now.getMonth(), now.getDayOfMonth());
        String formatDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        form.setDate(formatDate);

        LocalTime startTime = LocalTime.of(10, 0);
        LocalTime endTime = LocalTime.of(11, 0);
        Schedule schedule = new Schedule(startTime, endTime);
        scheduleRepository.save(schedule);
        List<Long> scheduleIds = new ArrayList<>();
        scheduleIds.add(schedule.getId());
        form.setScheduleIds(scheduleIds);

        CookingRoom cookingRoom = cookingRoomRepository.findByRoomNum(101).orElseThrow(() -> new IllegalArgumentException("해당하는 요리실이 없습니다."));
        form.setCookingRoomId(cookingRoom.getId());

        return form;
    }
}