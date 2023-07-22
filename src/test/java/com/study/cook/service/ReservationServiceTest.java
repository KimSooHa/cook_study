package com.study.cook.service;

import com.study.cook.controller.ReservationForm;
import com.study.cook.domain.*;
import com.study.cook.exception.FindCookingRoomException;
import com.study.cook.exception.FindReservationException;
import com.study.cook.exception.FindScheduleException;
import com.study.cook.exception.ReserveFailException;
import com.study.cook.repository.*;
import com.study.cook.util.DateParser;
import lombok.extern.slf4j.Slf4j;
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
import java.util.NoSuchElementException;
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
    ReservationRepository reservationRepository;

    @Autowired
    ScheduleRepository scheduleRepository;

    @Autowired
    CookingRoomRepository cookingRoomRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ClubRepository clubRepository;

    @Autowired
    CategoryRepository categoryRepository;

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
        ReservationForm form = setForm(101, 10);
        Member member = getMember();

        // when
        List<Long> reservations = reservationService.create(form, member);

        // then
        assertThat(reservations.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("요리실 예약 실패")
    void failToCreate() {
        // given
        Member member = getMember();
        for (int i = 0; i < 10; i++) {
            if(i >= 8)
                save(setForm(102, 10 + i), member);

            else {
                ReservationForm form = setForm(101, 10 + i);
                save(form, member);
            }
        }

        // when
        ReservationForm form = setForm(101, 10);

        // then
        // 전체 예약가능 수 초과로 인한 예약 실패
        assertThatThrownBy(() -> reservationService.create(form, member)).isInstanceOf(ReserveFailException.class);
    }

    @Test
    @DisplayName("예약된 요리실 목록 조회")
    void findList() {
        // given
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.Direction.DESC, "startDateTime");
        ReservationForm form = setForm(101, 10);
        Member member = getMember();
        List<Long> reservationIds = save(form, member);

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
        ReservationForm form = setForm(101, 10);
        Member member = getMember();
        List<Long> reservationIds = save(form, member);
        String date = form.getDate();

        // when
        List<Reservation> findReservations = reservationService.findByCookingRoomIdAndDate(cookingRoom.getId(), date).orElseThrow();

        // then
        assertThat(findReservations.size()).isEqualTo(1);
        assertThat(findReservations.get(0).getId()).isEqualTo(reservationIds.get(0));
    }

    @Test
    @DisplayName("아이디로 조회")
    void findOneById() {
        // given
        ReservationForm form = setForm(101, 10);
        Member member = getMember();
        List<Long> reservationIds = save(form, member);

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
        ReservationForm form = setForm(101, 10);
        save(form, member);
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
    @DisplayName("스터디와 매핑된 예약목록 조회")
    void findByClub() {
        // given
        Club club = new Club("테스트용 스터디", "Test", 5, "");
        Member member = getMember();
        Category category = categoryRepository.findAll().get(0);
        List<Reservation> reservations = reservationRepository.findAll();

        Club createdClub = Club.createClub(club, member, category, reservations);
        clubRepository.save(createdClub);

        // when
        List<Reservation> findReservations = reservationService.findByClub(club).get();

        // then
        assertThat(findReservations.size()).isEqualTo(reservations.size());
    }

    @Test
    @DisplayName("예약 수정")
    void update() {
        // given
        ReservationForm form = setForm(101, 10);
        Member member = getMember();
        List<Long> reservationIds = save(form, member);
        String date = form.getDate();

        // when
        LocalDateTime now = LocalDateTime.now();
        LocalDate newDate = LocalDate.of(now.getYear() + 1, now.getMonth(), now.getDayOfMonth());
        String formatDate = newDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        form.setDate(formatDate);
        reservationService.update(reservationIds.get(0), form);

        // then
        assertThat(form.getDate()).isNotEqualTo(date);
    }

    @Test
    @DisplayName("예약 삭제")
    void delete() {
        // given
        ReservationForm form = setForm(101, 10);
        Member member = getMember();
        List<Long> reservationIds = save(form, member);

        // when
        reservationService.delete(reservationIds.get(0));

        // then
        assertThat(reservationRepository.findById(reservationIds.get(0))).isNotPresent();
    }

    private ReservationForm setForm(int roomNum, int time) {
        ReservationForm form = new ReservationForm();
        LocalDateTime now = LocalDateTime.now();
        LocalDate date = LocalDate.of(now.getYear(), now.getMonth(), now.getDayOfMonth());
        String formatDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        form.setDate(formatDate);

        LocalTime startTime = LocalTime.of(time, 0);
        LocalTime endTime = LocalTime.of(time + 1, 0);
        Schedule schedule = new Schedule(startTime, endTime);
        scheduleRepository.save(schedule);
        List<Long> scheduleIds = new ArrayList<>();
        scheduleIds.add(schedule.getId());
        form.setScheduleIds(scheduleIds);

        CookingRoom cookingRoom = cookingRoomRepository.findByRoomNum(roomNum).orElseThrow(() -> new IllegalArgumentException("해당하는 요리실이 없습니다."));
        form.setCookingRoomId(cookingRoom.getId());

        return form;
    }

    private Member getMember() {
        return memberRepository.findByLoginId("test1").orElseThrow(() -> new IllegalArgumentException("해당하는 회원이 없습니다."));
    }

    private List<Long> save(ReservationForm form, Member member) {
        List<Long> reservationIds = new ArrayList<>();

        // 예약 갯수 10개 이상이면 못함
        Optional<List<Reservation>> recentReservations = reservationRepository.findByMemberIdAndDateTimeGt(member.getId(), LocalDateTime.now());
        if (recentReservations.isPresent()) {
            if (recentReservations.get().size() + form.getScheduleIds().size() > 10) {
                throw new ReserveFailException("전체 예약가능 수를 초과하였습니다. 현재 예약 가능 수는 " + (10 - recentReservations.get().size()) + "개 입니다.");
            }
        }

        for (Long scheduleId : form.getScheduleIds()) {

            // 문자를 날짜로 파싱
            LocalDate date = LocalDate.parse(form.getDate());
            Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(() -> new FindScheduleException("등록 실패: 해당 시간을 찾을 수 없습니다."));
            LocalTime startTime = schedule.getStartTime();
            LocalTime endTime = schedule.getEndTime();

            LocalDateTime startDateTime = dateParser.parseToDateTime(date, startTime);
            LocalDateTime endDateTime = dateParser.parseToDateTime(date, endTime);

            Reservation reservation = new Reservation(startDateTime, endDateTime);
            CookingRoom cookingRoom = cookingRoomRepository.findById(form.getCookingRoomId()).orElseThrow(() -> new NoSuchElementException("no such data"));
            Reservation.createReservation(reservation, member, cookingRoom);

            reservationRepository.save(reservation);
            reservationIds.add(reservation.getId());
        }

        return reservationIds;
    }
}