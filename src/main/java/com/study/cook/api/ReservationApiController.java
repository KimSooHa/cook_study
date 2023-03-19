package com.study.cook.api;

import com.study.cook.controller.ReservationForm;
import com.study.cook.domain.Member;
import com.study.cook.exception.FindCookingRoomException;
import com.study.cook.exception.FindReservationException;
import com.study.cook.exception.FindScheduleException;
import com.study.cook.exception.ReserveFailException;
import com.study.cook.service.ReservationService;
import com.study.cook.util.MemberFinder;
import com.study.cook.util.ResultVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cooking-rooms")
@Slf4j
public class ReservationApiController {

    private final ReservationService reservationService;
    private final MemberFinder memberFinder;

    /**
     * 요리실 예약
     */
    @PostMapping("/reservation")
    public ResultVO reserve(@RequestBody @Valid ReservationForm reservationForm, HttpSession session) {

        Member member = memberFinder.getMember(session);
        reservationService.create(reservationForm, member);

        return new ResultVO("등록에 성공했습니다.", "/cooking-rooms/reservations", true);
    }

    @PutMapping("/reservation/{reservationId}")
    public ResultVO update(@PathVariable Long reservationId, @RequestBody @Valid ReservationForm reservationForm, HttpSession session) {

        Member member = memberFinder.getMember(session);
        reservationService.update(reservationId, reservationForm, member);

        return new ResultVO("수정하였습니다!", "/cooking-rooms/reservations", true);
    }
}
