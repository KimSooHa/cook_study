package com.study.cook.service;

import com.study.cook.controller.ReservationForm;
import com.study.cook.controller.ScheduleForm;
import com.study.cook.controller.ScheduleListForm;
import com.study.cook.domain.*;
import com.study.cook.repository.CookingRoomRepository;
import com.study.cook.repository.ReservationRepository;
import com.study.cook.repository.ScheduleRepository;
import com.study.cook.util.DateParser;
import com.study.cook.util.MemberFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReservationService {

    private final CookingRoomRepository cookingRoomRepository;
    private final ScheduleRepository scheduleRepository;
    private final CookingRoomService cookingRoomService;
    private final ReservationRepository reservationRepository;
    private final DateParser dateParser;
    private final MemberFinder memberFinder;

    /**
     * 요리실 등록
     */
    @Transactional  // 변경해야 하기 때문에 읽기, 쓰기가 가능해야 함
    public List<Long> create(ReservationForm form, Member member) {

        List<Long> reservationIds = new ArrayList<>();
//        List<ScheduleForm> schedules = scheduleListForm.getScheduleForms();

        for (Long scheduleId : form.getScheduleIds()) {

            // 문자를 날짜로 파싱
            LocalDate date = LocalDate.parse(form.getDate());
//            LocalTime startTime = LocalTime.parse(time.get(0));
//            LocalTime endTime = LocalTime.parse(time.get(1));
            Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(() -> new IllegalArgumentException("no such data"));
            LocalTime startTime = schedule.getStartTime();
            LocalTime endTime = schedule.getEndTime();


            LocalDateTime startDateTime = dateParser.parseToDateTime(date, startTime);
            LocalDateTime endDateTime = dateParser.parseToDateTime(date, endTime);

            Reservation reservation = new Reservation(startDateTime, endDateTime);
            CookingRoom cookingRoom = cookingRoomRepository.findById(form.getCookingRoomId()).orElseThrow(() -> new IllegalArgumentException("no such data"));
            Reservation.createReservation(reservation, member, cookingRoom);

            reservationRepository.save(reservation);
            reservationIds.add(reservation.getId());
        }

        return reservationIds;
    }


    /**
     * 전체 조회
     */
    public List<Reservation> findList() {
        return reservationRepository.findAll();
    }

    public Optional<List<Reservation>> findByCookingRoomIdAndDate(Long cookingRoomId, String date) {
        return reservationRepository.findByCookingRoomAndStartDate(cookingRoomId, date);
    }


    /**
     * 단건 조회
     */
    public Reservation findOneById(Long reservationId) {
        return reservationRepository.findById(reservationId).orElse(null);
    }

    public Optional<List<Reservation>> findByDateAndCookingRoom(LocalDateTime startDateTime, int cookingRoomNum) {
        CookingRoom cookingRoom = cookingRoomRepository.findByRoomNum(cookingRoomNum).orElseThrow(() -> new IllegalArgumentException("no such data"));
        return reservationRepository.findByStartDateTimeAndCookingRoom(startDateTime, cookingRoom);
    }

    public Optional<List<Reservation>> findByMember(Member member) {
        return reservationRepository.findByMemberId(member.getId());
    }

    public Optional<List<Reservation>> findByClub(Club club) {
        return reservationRepository.findByClubId(club.getId());
    }


    @Transactional
    public void update(Long id, ReservationForm form, Member member) {

        // 문자를 날짜로 파싱
        LocalDate date = LocalDate.parse(form.getDate());

        Schedule schedule = scheduleRepository.findById(form.getScheduleIds().get(0)).orElseThrow(() -> new NoSuchElementException());
        LocalTime startTime = schedule.getStartTime();
        LocalTime endTime = schedule.getEndTime();

        LocalDateTime startDateTime = dateParser.parseToDateTime(date, startTime);
        LocalDateTime endDateTime = dateParser.parseToDateTime(date, endTime);

        Reservation reservation = reservationRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("no such data"));
        reservation.setStartDateTime(startDateTime);
        reservation.setEndDateTime(endDateTime);
        CookingRoom cookingRoom = cookingRoomRepository.findById(form.getCookingRoomId()).orElseThrow(() -> new IllegalArgumentException("no such data"));
        reservation.setCookingRoom(cookingRoom);


    }

    @Transactional
    public void delete(Long reservationId) {
        Reservation reservation = findOneById(reservationId);
        reservationRepository.delete(reservation);
    }

}
