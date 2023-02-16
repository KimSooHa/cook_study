package com.study.cook.controller;

import com.study.cook.SessionConst;
import com.study.cook.domain.Category;
import com.study.cook.domain.Club;
import com.study.cook.domain.Member;
import com.study.cook.domain.Reservation;
import com.study.cook.dto.ClubDto;
import com.study.cook.dto.ClubListDto;
import com.study.cook.dto.ReservationDto;
import com.study.cook.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final ParticipationService participationService;


    @GetMapping("/{clubId}")
    public String detail(@PathVariable Long clubId, Model model, HttpServletRequest request) {
        Club club = clubService.findOneById(clubId);

        // 남은 인원수 조회 로직 추가하기
        int restCount = (int) (club.getMaxCount() - participationService.countByClub(club));


        Member member = memberService.findOneById(club.getMember().getId());
        // 쿡스터디에 지정된 예약한 요리실 일정
        Optional<List<Reservation>> reservations = reservationService.findByClub(club);
        Category category = categoryService.findOneById(club.getCategory().getId());

        if (reservations.isPresent()) {
            List<ReservationDto> reservationDtos = new ArrayList<>();
            for (Reservation reservation : reservations.get()) {
                LocalDateTime startDateTime = reservation.getStartDateTime();
                LocalDateTime endDateTime = reservation.getEndDateTime();

                String formatDate = getFormatDate(startDateTime);
                String formatStartTime = getFormatTime(startDateTime);
                String formatEndTime = getFormatTime(endDateTime);

                ReservationDto reservationDto = new ReservationDto(reservation.getId(), formatDate, formatStartTime, formatEndTime, reservation.getCookingRoom().getRoomNum());
                reservationDtos.add(reservationDto);
            }
            model.addAttribute("reservationDtos", reservationDtos);
        }

        ClubDto clubDto = new ClubDto(club.getName(),club.getIntroduction(), club.getStatus(), club.getMaxCount(), restCount, club.getPrice(), club.getIngredients());

        model.addAttribute("clubDto", clubDto);
        model.addAttribute("memberName", member.getName());
        model.addAttribute("categoryName", category.getName());

        Member loginMember = getMember(request);

        if (member.getId() == loginMember.getId()) {    // 로그인한 회원이 쿡스터디 등록한 회원이라면
            return "club/detail-manager";
        }
        return "club/detail-participant";

    }

    @GetMapping("/participations")
    public String joinList(HttpServletRequest request, Model model, Pageable pageable) {

        Member member = getMember(request);
        Page<ClubListDto> participateClubs = clubService.findByParticipant(member.getId(), pageable);

        model.addAttribute("participateClubs", participateClubs);

        // 참여하는 스터디 리스트 페이지 만들기!
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

                if(LocalDateTime.now().isAfter(startDateTime))
                    continue;

                String formatDate = getFormatDate(startDateTime);
                String formatStartTime = getFormatTime(startDateTime);
                String formatEndTime = getFormatTime(endDateTime);

                ReservationDto reservationDto = new ReservationDto(reservation.getId(), formatDate, formatStartTime, formatEndTime, reservation.getCookingRoom().getRoomNum());
                reservationDtos.add(reservationDto);

            }
            model.addAttribute("reservationDtos", reservationDtos);
        }

        return "club/create-form";
    }


    @PostMapping
    public String create(@Valid ClubForm form, BindingResult result) {
        if (result.hasErrors()) {
            return "club/create-form";
        }

        Club club = new Club(form.getName(), form.getIntroduction(), form.getMaxCount(), form.getIngredients());

        Member member = memberService.findOneById(form.getMemberId());
        Category category = categoryService.findOneById(form.getCategory().getId());

        Club createdClub = Club.createClub(club, member, category, form.getReservations());

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
        form.setReservations(club.getReservations());

        model.addAttribute("form", form);
        model.addAttribute("reservations", reservations);
        return "/update-form";
    }

    @PutMapping("/{clubId}")
    public String update(@PathVariable Long clubId, @Valid ClubForm form, BindingResult result, RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "club/update-form";
        }
        clubService.update(clubId, form.getName(), form.getIntroduction(), form.getMaxCount(), form.getIngredients(), form.getCategory(), form.getReservations());
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
