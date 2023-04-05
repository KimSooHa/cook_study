package com.study.cook.repository;

import com.study.cook.domain.Reservation;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepositoryCustom {

    Optional<List<Reservation>> findByMemberIdAndDateTimeGt(Long memberId, LocalDateTime today);
    Optional<List<Reservation>> findByCookingRoomAndStartDate(Long cookingRoomId, String startDate);

}
