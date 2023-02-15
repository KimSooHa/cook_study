package com.study.cook.controller;

import com.study.cook.SessionConst;
import com.study.cook.domain.Category;
import com.study.cook.domain.Club;
import com.study.cook.domain.Member;
import com.study.cook.domain.Reservation;
import com.study.cook.dto.ClubDto;
import com.study.cook.dto.ReservationDto;
import com.study.cook.service.CategoryService;
import com.study.cook.service.ClubService;
import com.study.cook.service.MemberService;
import com.study.cook.service.ReservationService;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/clubs")
@Slf4j
public class ClubController {

    private final ClubService clubService;
    private final MemberService memberService;
    private final CategoryService categoryService;
    private final ReservationService reservationService;


    @GetMapping("/{clubId}")
    public String detail(@PathVariable Long clubId, Model model) {
        Club club = clubService.findOneById(clubId);

        // 남은 인원수 조회 로직 추가하기
//        club.getMaxCount() -


        Member member = memberService.findOneById(club.getMember().getId());
        // 예약한 요리실 일정
        Reservation reservation = reservationService.findOneById(club.getReservation().getId());
        Category category = categoryService.findOneById(club.getCategory().getId());

        LocalDateTime startDateTime = reservation.getStartDateTime();
        LocalDateTime endDateTime = reservation.getEndDateTime();

        String formatDate = getFormatDate(startDateTime);
        String formatStartTime = getFormatTime(startDateTime);
        String formatEndTime = getFormatTime(endDateTime);

//        ClubDto clubDto = new ClubDto(club.getName(),club.getIntroduction(), club.getStatus(), club.getMaxCount(), club.restCount, club.getPrice(), club.getIngredients(), reservation.getId(), formatDate, formatStartTime, formatEndTime, reservation.getCookingRoom().getRoomNum());
//
//        model.addAttribute("clubDto", clubDto);
        model.addAttribute("memberName", member.getName());
        model.addAttribute("categoryName", category.getName());


        return "";
    }


    @GetMapping
    public String createForm(Model model, HttpServletRequest request) {
        model.addAttribute("clubForm", new ClubForm());
        Optional<List<Reservation>> reservations = reservationService.findByMember(getMember(request));

        if (reservations.isPresent()) {
            List<ReservationDto> reservationDtos = new ArrayList<>();
            for (Reservation reservation : reservations.get()) {
                LocalDateTime startDateTime = reservation.getStartDateTime();
                LocalDateTime endDateTime = reservation.getEndDateTime();

                String formatDate = getFormatDate(startDateTime);
                String formatStartTime = getFormatTime(startDateTime);
                String formatEndTime = getFormatTime(endDateTime);

                ReservationDto reservationDto = new ReservationDto(reservation.getId(), formatDate, formatStartTime, formatEndTime, reservation.getCookingRoom().getRoomNum(), reservation.getStatus());
                reservationDtos.add(reservationDto);

            }
            model.addAttribute("reservationDtos", reservationDtos);
        }

        return "club/create-form";
    }


    @PostMapping
    public String create(@Valid ClubForm form, Long reservationId, BindingResult result) {
        if (result.hasErrors()) {
            return "club/create-form";
        }

        Club club = new Club(form.getName(), form.getIntroduction(), form.getMaxCount(), form.getIngredients());

        Member member = memberService.findOneById(form.getMemberId());
        Category category = categoryService.findOneById(form.getCategory().getId());
        Reservation reservation = reservationService.findOneById(reservationId);
        Club createdClub = Club.createClub(club, member, category, reservation);

        model.addAttribute("form", form);
        model.addAttribute("reservations", reservations);

        clubService.create(createdClub);
        return "redirect:/clubs/{clubId}/detail";
    }

    @GetMapping("/{clubId}/edit")
    public String update(@PathVariable Long clubId, Model model) {
        Club club = clubService.findOneById(clubId);
        Optional<List<Reservation>> reservations = reservationService.findByMember(club.getMember());
        ClubForm form = new ClubForm();
        form.setName(club.getName());
        form.setIntroduction(club.getIntroduction());
        form.setMaxCount(club.getMaxCount());
        form.setIngredients(club.getIngredients());
        form.setMemberId(club.getMember().getId());
        form.setCategory(club.getCategory());
        form.setReservation(club.getReservation());

        model.addAttribute("form", form);
        model.addAttribute("reservations", reservations);
        return "/update-form";
    }

    @PutMapping("/{clubId}")
    public String update(@PathVariable Long clubId, @Valid ClubForm form, BindingResult result, RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "club/update-form";
        }
        clubService.update(clubId, form.getName(), form.getIntroduction(), form.getMaxCount(), form.getIngredients(), form.getCategory(), form.getReservation());
        redirectAttributes.addAttribute("clubId", clubId);
        redirectAttributes.addAttribute("status", true);
        return "redirect:/clubs/{clubId}";
    }

    @DeleteMapping("/{clubId}")
    public String delete(@PathVariable Long clubId) {
        Club club = clubService.findOneById(clubId);
        clubService.delete(club);

        return "redirect:/";
    }

    // DateTime -> String
    private static String getFormatTime(LocalDateTime time) {
        return LocalTime.of(time.getHour(), time.getMinute()).format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    private static String getFormatDate(LocalDateTime date) {
        return LocalDate.of(date.getYear(), date.getMonth(), date.getDayOfMonth()).format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
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
