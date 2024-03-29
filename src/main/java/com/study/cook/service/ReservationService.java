package com.study.cook.service;

import com.study.cook.controller.ReservationForm;
import com.study.cook.domain.*;
import com.study.cook.exception.FindCookingRoomException;
import com.study.cook.exception.FindReservationException;
import com.study.cook.exception.FindScheduleException;
import com.study.cook.exception.ReserveFailException;
import com.study.cook.repository.CookingRoomRepository;
import com.study.cook.repository.ReservationRepository;
import com.study.cook.repository.ScheduleRepository;
import com.study.cook.util.DateParser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final ReservationRepository reservationRepository;
    private final DateParser dateParser;

    /**
     * 요리실 예약
     */
    @Transactional  // 변경해야 하기 때문에 읽기, 쓰기가 가능해야 함
    public List<Long> create(ReservationForm form, Member member) {

        List<Long> reservationIds = new ArrayList<>();

        // 예약 갯수 10개 이상이면 못함
        Optional<List<Reservation>> recentReservations = findByMemberAndDateGt(member, LocalDateTime.now());
        if (recentReservations.isPresent()) {
            if (recentReservations.get().size() + form.getScheduleIds().size() > 10) {
                throw new ReserveFailException("전체 예약가능 수를 초과하였습니다. 현재 예약 가능 수는 " + (10-recentReservations.get().size()) + "개 입니다.");
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


    /**
     * 전체 조회
     */
    public Page<Reservation> findList(Pageable pageable) {
        return reservationRepository.findAll(pageable);
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

    public Optional<Page<Reservation>> findByMember(Member member, Pageable pageable) {
        return reservationRepository.findByMemberId(member.getId(), pageable);
    }

    // 로그인한 멤버, 현재 날짜보다 뒤의 예약한 리스트 조회
    public Optional<List<Reservation>> findByMemberAndDateGt(Member member, LocalDateTime today) {
        return reservationRepository.findByMemberIdAndDateTimeGt(member.getId(), today);
    }

    public Optional<List<Reservation>> findByClub(Club club) {
        return reservationRepository.findByClubId(club.getId());
    }


    @Transactional
    public void update(Long id, ReservationForm form) {

        // 문자를 날짜로 파싱
        LocalDate date = LocalDate.parse(form.getDate());

        Schedule schedule = scheduleRepository.findById(form.getScheduleIds().get(0)).orElseThrow(() -> new FindScheduleException("수정 실패: 해당 시간을 찾을 수 없습니다."));
        LocalTime startTime = schedule.getStartTime();
        LocalTime endTime = schedule.getEndTime();

        LocalDateTime startDateTime = dateParser.parseToDateTime(date, startTime);
        LocalDateTime endDateTime = dateParser.parseToDateTime(date, endTime);

        Reservation reservation = reservationRepository.findById(id).orElseThrow(() -> new FindReservationException("수정 실패: 예약을 찾을 수 없습니다."));
        reservation.setStartDateTime(startDateTime);
        reservation.setEndDateTime(endDateTime);
        CookingRoom cookingRoom = cookingRoomRepository.findById(form.getCookingRoomId()).orElseThrow(() -> new FindCookingRoomException("수정 실패: 해당 요리실을 찾을 수 없습니다."));
        reservation.setCookingRoom(cookingRoom);

    }

    @Transactional
    public void delete(Long reservationId) {
        Reservation reservation = findOneById(reservationId);
        reservationRepository.delete(reservation);
    }

}
