package com.study.cook.service;

import com.study.cook.controller.ReservationForm;
import com.study.cook.domain.*;
import com.study.cook.exception.FindCookingRoomException;
import com.study.cook.exception.FindReservationException;
import com.study.cook.repository.ClubRepository;
import com.study.cook.repository.CookingRoomRepository;
import com.study.cook.repository.MemberRepository;
import com.study.cook.repository.ScheduleRepository;
import com.study.cook.util.DateParser;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@Slf4j
class ReservationServiceTest {

    @Autowired
    ReservationService reservationService;

    @Autowired
    ScheduleRepository scheduleRepository;

    @Autowired
    CookingRoomRepository cookingRoomRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ClubRepository clubRepository;

    @Autowired
    DateParser dateParser;

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
        Member member = getMember();

        // when
        List<Long> reservations = reservationService.create(form, member);

        // then
        assertThat(reservations.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("예약된 요리실 목록 조회")
    void findList() {
        // given
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.Direction.DESC, "startDateTime");
        ReservationForm form = setForm();
        Member member = getMember();
        List<Long> reservationIds = reservationService.create(form, member);

        // when
        Page<Reservation> list = reservationService.findList(pageRequest);
        List<Reservation> reservations = list.toList();

        // then
        assertThat(list.getSize()).isEqualTo(10);
        assertThat(reservations.get(0).getId()).isEqualTo(reservationIds.get(0));
    }

    @Test
    @DisplayName("요리실과 시작 날짜로 조회")
    void findByCookingRoomIdAndDate() {
        // given
        CookingRoom cookingRoom = cookingRoomRepository.findByRoomNum(101).orElseThrow(() -> new FindCookingRoomException("해당하는 요리실이 없습니다."));
        ReservationForm form = setForm();
        Member member = getMember();
        List<Long> reservations = reservationService.create(form, member);
        String date = form.getDate();

        // when
        List<Reservation> findReservations = reservationService.findByCookingRoomIdAndDate(cookingRoom.getId(), date).orElseThrow();

        // then
        assertThat(findReservations.size()).isEqualTo(1);
        assertThat(findReservations.get(0).getId()).isEqualTo(reservations.get(0));
    }

    @Test
    @DisplayName("아이디로 조회")
    void findOneById() {
        // given
        ReservationForm form = setForm();
        Member member = getMember();
        List<Long> reservationIds = reservationService.create(form, member);

        // when
        Reservation reservation = reservationService.findOneById(reservationIds.get(0));

        // then
        assertThat(reservation.getId()).isEqualTo(reservationIds.get(0));
    }

    @Test
    void findByDateAndCookingRoom() {
    }

    @Test
    @DisplayName("회원으로 예약목록 조회")
    void findByMember() {
        // given
        Member member = getMember();
        ReservationForm form = setForm();
        reservationService.create(form, member);
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.Direction.DESC, "startDateTime");

        // when
        Page<Reservation> reservations = reservationService.findByMember(member, pageRequest).orElseThrow(() -> new FindReservationException("해당하는 회원의 예약 목록이 없습니다."));

        // then
        assertThat(reservations.get().count()).isEqualTo(1);
    }

    @Test
    @DisplayName("회원과 현재 날짜보다 뒤의 목록 조회")
    void findByMemberAndDateGt() {
        // given
        Member member = getMember();
        LocalDateTime now = LocalDateTime.now();

        // when
        Optional<List<Reservation>> reservations = reservationService.findByMemberAndDateGt(member, now);

        // then
        assertThat(reservations.get()).isEmpty();
    }

    @Test
    void findByClub() {
    }

    @Test
    @DisplayName("예약 수정")
    void update() {
        // given
        ReservationForm form = setForm();
        Member member = getMember();
        List<Long> reservations = reservationService.create(form, member);
        String date = form.getDate();

        // when
        LocalDateTime now = LocalDateTime.now();
        LocalDate newDate = LocalDate.of(now.getYear()+1, now.getMonth(), now.getDayOfMonth());
        String formatDate = newDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        form.setDate(formatDate);
        reservationService.update(reservations.get(0), form);

        // then
        assertThat(form.getDate()).isNotEqualTo(date);
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

    private Member getMember() {
        return memberRepository.findByLoginId("test1").orElseThrow(() -> new IllegalArgumentException("해당하는 회원이 없습니다."));
    }
}