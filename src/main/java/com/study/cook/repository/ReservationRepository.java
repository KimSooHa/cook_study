package com.study.cook.repository;

import com.study.cook.domain.CookingRoom;
import com.study.cook.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface ReservationRepository extends JpaRepository<Reservation, Long>, ReservationRepositoryCustom {


    Optional<List<Reservation>> findByStartDateTimeAndCookingRoom(LocalDateTime startDateTime, CookingRoom cookingRoom);

    Optional<List<Reservation>> findByMemberId(Long memberId);

    Optional<List<Reservation>> findByClubId(Long clubId);
}
