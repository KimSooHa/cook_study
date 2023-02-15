package com.study.cook.service;

import com.study.cook.domain.CookingRoom;
import com.study.cook.domain.Member;
import com.study.cook.domain.Reservation;
import com.study.cook.domain.Schedule;
import com.study.cook.repository.CookingRoomRepository;
import com.study.cook.repository.ReservationRepository;
import com.study.cook.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReservationService {

    private final CookingRoomRepository cookingRoomRepository;
    private final ScheduleRepository scheduleRepository;
    private final ReservationRepository reservationRepository;

    /**
     * 요리실 등록
     */
    @Transactional  // 변경해야 하기 때문에 읽기, 쓰기가 가능해야 함
    public Long create(Reservation reservation, Member member, CookingRoom cookingRoom) {

        Reservation.createReservation(reservation, member, cookingRoom);

        reservationRepository.save(reservation);

        return reservation.getId();
    }


    /**
     * 전체 조회
     */
    public List<Reservation> findList() {
        return reservationRepository.findAll();
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


    @Transactional
    public void update(Long id, LocalDateTime startDateTime, LocalDateTime endDateTime, int cookingRoomNum) {
        Reservation reservation = reservationRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("no such data"));
        reservation.setStartDateTime(startDateTime);
        reservation.setEndDateTime(endDateTime);
        CookingRoom cookingRoom = cookingRoomRepository.findByRoomNum(cookingRoomNum).orElseThrow(() -> new IllegalArgumentException("no such data"));
        reservation.setCookingRoom(cookingRoom);
    }

    @Transactional
    public void delete(Reservation reservation) {
        reservationRepository.delete(reservation);
    }

}
