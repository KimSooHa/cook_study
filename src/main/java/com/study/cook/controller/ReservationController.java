package com.study.cook.controller;

import com.study.cook.SessionConst;
import com.study.cook.domain.*;
import com.study.cook.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cooking-rooms")
@Slf4j
public class ReservationController {

    private final MemberService memberService;
    private final ReservationService reservationService;
    private final CookingRoomService cookingRoomService;
    private final ScheduleService scheduleService;

    @GetMapping("/reservations")
    public String list(HttpServletRequest request, Model model) {
        Optional<List<Reservation>> list = reservationService.findByMember(getMember(request));

        model.addAttribute("reservations", list);

        // 예약 리스트 페이지 만들기!
        return "";
    }

    /**
     * 요리실 예약
     */
    @GetMapping("/reservation")
    public String reserveForm(Model model) {
        List<CookingRoom> cookingRooms = cookingRoomService.findList();
        List<Schedule> schedules = scheduleService.findListByCookingRoom(cookingRooms.get(0));
        model.addAttribute("reservationForm", new ReservationForm());
        model.addAttribute("cookingRooms", cookingRooms);
        model.addAttribute("schedules", schedules);

        return "reservation/create-form";
    }

    @PostMapping("/reservation")
    public String reserve(@Valid ReservationForm form, BindingResult result, HttpServletRequest request) {
        if (result.hasErrors()) {
            return "reservation/create-form";
        }
        for (List<String> time : form.getTimes()) {

            // 문자를 날짜로 파싱
            LocalDate date = LocalDate.parse(form.getDate());
            LocalTime startTime = LocalTime.parse(time.get(0));
            LocalTime endTime = LocalTime.parse(time.get(1));

            LocalDateTime startDateTime = parseToDateTime(date, startTime);
            LocalDateTime endDateTime = parseToDateTime(date, endTime);

            Reservation reservation = new Reservation(startDateTime, endDateTime, ReservationStatus.WAIT);
            CookingRoom cookingRoom = cookingRoomService.findOneByRoomNum(form.getCookingRoomNum());

            Member member = getMember(request);

            reservationService.create(reservation, member, cookingRoom);
        }
        // 예약 리스트 페이지 만들기
        return "redirect:/mypage";
    }


    @PutMapping("/{reservationId}")
    public String update(@PathVariable Long reservationId, @Valid ReservationForm form, BindingResult result, RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "reservation/update-form";
        }

        for (List<String> time : form.getTimes()) {
            // 문자를 날짜로 파싱
            LocalDate date = LocalDate.parse(form.getDate());
            LocalTime startTime = LocalTime.parse(time.get(0));
            LocalTime endTime = LocalTime.parse(time.get(1));

            LocalDateTime startDateTime = parseToDateTime(date, startTime);
            LocalDateTime endDateTime = parseToDateTime(date, endTime);

            reservationService.update(reservationId, startDateTime, endDateTime, form.getCookingRoomNum());
        }
        redirectAttributes.addAttribute("reservationId", reservationId);
        redirectAttributes.addAttribute("status", true);
        return "redirect:/reservations/{reservationId}";
    }

    @DeleteMapping("/{reservationId}")
    public String delete(@PathVariable Long reservationId) {
        Reservation reservation = reservationService.findOneById(reservationId);
        reservationService.delete(reservation);

        return "redirect:/reservations";
    }

    private static LocalDateTime parseToDateTime(LocalDate date, LocalTime time) {
        LocalDateTime dateTime = LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), time.getHour(), time.getMinute());
        return dateTime;
    }


    // 로그인 회원 찾기
    private Member getMember(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        // 세션에 로그인 회원 정보 조회
        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        Member member = memberService.findOneById(loginMember.getId());
        return member;
    }

}
