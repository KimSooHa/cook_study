package com.study.cook.controller;

import com.study.cook.domain.CookingRoom;
import com.study.cook.domain.Reservation;
import com.study.cook.domain.Schedule;
import com.study.cook.dto.ReservationListDto;
import com.study.cook.service.CookingRoomService;
import com.study.cook.service.ReservationService;
import com.study.cook.service.ScheduleService;
import com.study.cook.util.DateParser;
import com.study.cook.util.MemberFinder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cooking-rooms")
@Slf4j
public class ReservationController {

    private final ReservationService reservationService;
    private final CookingRoomService cookingRoomService;
    private final ScheduleService scheduleService;
    private final DateParser dateParser;
    private final MemberFinder memberFinder;

    @GetMapping("/reservations")
    public String list(Model model, RedirectAttributes redirectAttributes,
                       @PageableDefault(size = 10, sort = "startDateTime", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Reservation> list = reservationService.findByMember(memberFinder.getMember(), pageable).orElseThrow();

        if (list.isEmpty()) {
            redirectAttributes.addFlashAttribute("msg", "예약한 요리실이 없습니다.");
            return "redirect:/";
        }

        // 페이지 dto로 변환
//        Page<ReservationListDto> reservations = list.map(ReservationListDto::toDtoList);
        Page<ReservationListDto> reservations = list.map(m -> ReservationListDto.builder()
                .id(m.getId())
                .date(dateParser.getFormatDate(m.getStartDateTime()))
                .startTime(dateParser.getFormatTime(m.getStartDateTime()))
                .endTime(dateParser.getFormatTime(m.getEndDateTime()))
                .cookingRoomName(m.getCookingRoom().getRoomNum())
                .build());

        model.addAttribute("reservations", reservations);
        model.addAttribute("maxPage", 4);   // 한 페이지 바 당 보여줄 최대 페이지 수


        return "reservation/list";
    }

    /**
     * 요리실 예약
     */
    @GetMapping("/reservation")
    public String reserveForm(Model model) {

        List<CookingRoom> cookingRooms = cookingRoomService.findList();
        List<Schedule> scheduleList = scheduleService.findListByCookingRoom(cookingRooms.get(0));
        List<ScheduleForm> schedules = scheduleList.stream().map(m -> {
                    LocalTime startTime = m.getStartTime();
                    LocalTime endTime = m.getEndTime();

                    String formatStartTime = dateParser.getFormatTime(startTime);
                    String formatEndTime = dateParser.getFormatTime(endTime);

                    ScheduleForm scheduleForm = new ScheduleForm();
                    scheduleForm.setScheduleId(m.getId());
                    scheduleForm.setStartDate(formatStartTime);
                    scheduleForm.setEndDate(formatEndTime);

                    return scheduleForm;
                }).collect(Collectors.toList());


        model.addAttribute("reservationForm", new ReservationForm());
        model.addAttribute("cookingRooms", cookingRooms);
        model.addAttribute("schedules", schedules);

        // 예약 갯수 10개 이상이면 못함
        Optional<List<Reservation>> recentReservations = reservationService.findByMemberAndDateGt(memberFinder.getMember(), LocalDateTime.now());
        if (recentReservations.isPresent()) {
            if (recentReservations.get().size() >= 10) {
                model.addAttribute("msg", "예약은 최대 10개까지만 가능합니다.");
                model.addAttribute("url", "/");
                return "reservation/create-form";
            }
        }

        return "reservation/create-form";
    }

    @GetMapping("/reservation/{reservationId}")
    public String update(@PathVariable Long reservationId, Model model) {
        Reservation reservation = reservationService.findOneById(reservationId);
        ReservationForm form = new ReservationForm();

        form.setId(reservationId);
        // 날짜 세팅
        LocalDateTime startDateTime = reservation.getStartDateTime();
        form.setDate(dateParser.getFormatDateDash(startDateTime));

        // 요리실 세팅
        CookingRoom cookingRoom = reservation.getCookingRoom();
        form.setCookingRoomId(cookingRoom.getId());

        // 스케줄 세팅
        Schedule findSchedule = scheduleService.findOneByCookingRoomAndStartTime(cookingRoom, dateParser.parseToTime(startDateTime));
        form.getScheduleIds().add(findSchedule.getId());

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

    @DeleteMapping("/reservation/{reservationId}")
    public String delete(@PathVariable Long reservationId) {

        reservationService.delete(reservationId);

        return "redirect:/cooking-rooms/reservations";
    }
}
