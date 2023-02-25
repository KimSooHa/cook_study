package com.study.cook.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.cook.domain.CookingRoom;
import com.study.cook.domain.Member;
import com.study.cook.domain.Reservation;
import com.study.cook.domain.Schedule;
import com.study.cook.service.CookingRoomService;
import com.study.cook.service.MemberService;
import com.study.cook.service.ReservationService;
import com.study.cook.service.ScheduleService;
import com.study.cook.util.DateParser;
import com.study.cook.util.JsonMaker;
import com.study.cook.util.MemberFinder;
import com.study.cook.util.ResultVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cooking-rooms")
@Slf4j
public class ReservationController {

    private final MemberService memberService;
    private final ReservationService reservationService;
    private final CookingRoomService cookingRoomService;
    private final ScheduleService scheduleService;
    private final DateParser dateParser;
    private final MemberFinder memberFinder;
    private final JsonMaker jsonMaker;

    @GetMapping("/reservations")
    public String list(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        List<Reservation> list = reservationService.findByMember(memberFinder.getMember(session)).orElseThrow();
        List<ReservationListForm> reservations = new ArrayList<>();
        if (list.isEmpty()) {
            model.addAttribute("msg", "예약한 요리실이 없습니다.");
            model.addAttribute("url", "/");
            return "reservation/list";
        }
        for (Reservation reservation : list) {
            ReservationListForm reservationListForm = new ReservationListForm();
            reservationListForm.setId(reservation.getId());
            reservationListForm.setDate(dateParser.getFormatDate(reservation.getStartDateTime()));
            reservationListForm.setStartTime(dateParser.getFormatTime(reservation.getStartDateTime()));
            reservationListForm.setEndTime(dateParser.getFormatTime(reservation.getEndDateTime()));
            reservationListForm.setCookingRoomName(reservation.getCookingRoom().getRoomNum());

            reservations.add(reservationListForm);
        }

        model.addAttribute("reservations", reservations);

        // 예약 리스트 페이지 만들기!
        return "reservation/list";
    }

    /**
     * 요리실 예약
     */
    @GetMapping("/reservation")
    public String reserveForm(Model model) {
        List<CookingRoom> cookingRooms = cookingRoomService.findList();
        List<Schedule> scheduleList = scheduleService.findListByCookingRoom(cookingRooms.get(0));

        ScheduleListForm scheduleListForm = new ScheduleListForm();
        List<ScheduleForm> schedules = new ArrayList<>();
        for (Schedule schedule : scheduleList) {
            LocalTime startTime = schedule.getStartTime();
            LocalTime endTime = schedule.getEndTime();

            String formatStartTime = dateParser.getFormatTime(startTime);
            String formatEndTime = dateParser.getFormatTime(endTime);

            ScheduleForm scheduleForm = new ScheduleForm();
            scheduleForm.setScheduleId(schedule.getId());
            scheduleForm.setStartDate(formatStartTime);
            scheduleForm.setEndDate(formatEndTime);
            schedules.add(scheduleForm);
        }
//        scheduleListForm.setScheduleForms(schedules);
//        scheduleListForm.setName("예약가능 시간");

        model.addAttribute("reservationForm", new ReservationForm());
        model.addAttribute("cookingRooms", cookingRooms);
//        model.addAttribute("scheduleListForm", scheduleListForm);
        model.addAttribute("schedules", schedules);

        return "reservation/create-form";
    }


    @ResponseBody
    @PostMapping("/reservation")
    // , consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE} / , consumes = "application/json" / MediaType.APPLICATION_FORM_URLENCODED_VALUE
    public ResultVO reserve(Model model,
                          @RequestBody @Valid ReservationForm reservationForm,
                          HttpSession session, RedirectAttributes redirectAttributes) throws JsonProcessingException {

        log.info("reservationForm={}", reservationForm);
        Member member = memberFinder.getMember(session);
        ResultVO resultVO;
        try {
            List<Long> id = reservationService.create(reservationForm, member);
        } catch (NoSuchElementException e) {
            resultVO = new ResultVO("등록에 실패했습니다.", "/cooking-rooms/reservation", false);
        }

        log.info("예약 성공!");
        resultVO = new ResultVO("등록에 성공했습니다.", "/cooking-rooms/reservations", true);

        return resultVO;
    }


    @GetMapping("/reservation/{reservationId}")
    public String update(@PathVariable Long reservationId, Model model) {
        Reservation reservation = reservationService.findOneById(reservationId);
        ReservationForm form = new ReservationForm();

        form.setId(reservationId);
        // 날짜 세팅
        LocalDateTime startDateTime = reservation.getStartDateTime();
        form.setDate(dateParser.getFormatDate(startDateTime));

        // 요리실 세팅
        CookingRoom cookingRoom = reservation.getCookingRoom();
        form.setCookingRoomId(cookingRoom.getId());

        // 스케줄 세팅
//        Schedule findSchedule = scheduleService.findListByCookingRoomAndStartTime(cookingRoom, dateParser.parseToTime(startDateTime));
//        form.getScheduleIds().add(findSchedule.getId());

        List<CookingRoom> cookingRooms = cookingRoomService.findList();
        List<Schedule> scheduleList = scheduleService.findListByCookingRoom(cookingRooms.get(0));

        // 폼 세팅
        List<ScheduleForm> schedules = new ArrayList<>();
        for (Schedule schedule : scheduleList) {
            LocalTime startTime = schedule.getStartTime();
            LocalTime endTime = schedule.getEndTime();

            String formatStartTime = dateParser.getFormatTime(startTime);
            String formatEndTime = dateParser.getFormatTime(endTime);

            ScheduleForm scheduleForm = new ScheduleForm();
            scheduleForm.setScheduleId(schedule.getId());
            scheduleForm.setStartDate(formatStartTime);
            scheduleForm.setEndDate(formatEndTime);
            schedules.add(scheduleForm);
        }
        model.addAttribute("reservationForm", form);
        model.addAttribute("cookingRooms", cookingRooms);
        model.addAttribute("schedules", schedules);

        return "reservation/update-form";
    }

    @ResponseBody
    @PutMapping("/reservation/{reservationId}")
    public ResultVO update(@PathVariable Long reservationId, @RequestBody @Valid ReservationForm reservationForm,
                         BindingResult result, HttpSession session) {

        ResultVO resultVO;
        if (result.hasErrors()) {
            resultVO = new ResultVO();
            resultVO.setMsg("수정에 실패했습니다.");
            resultVO.setUrl("/cooking-rooms/reservation/" + reservationId);
        }

        Member member = memberFinder.getMember(session);
        try {
            reservationService.update(reservationId, reservationForm, member);
        } catch (NoSuchElementException e) {
            resultVO = new ResultVO();
            resultVO.setMsg("수정에 실패했습니다.");
            resultVO.setUrl("/cooking-rooms/reservation/" + reservationId);
        }

        resultVO = new ResultVO();
        resultVO.setMsg("수정하였습니다!");
        resultVO.setUrl("/cooking-rooms/reservations");

        return resultVO;
    }

    @DeleteMapping("/reservation/{reservationId}")
    public String delete(@PathVariable Long reservationId) {

        reservationService.delete(reservationId);

        return "redirect:/cooking-rooms/reservations";
    }


}
