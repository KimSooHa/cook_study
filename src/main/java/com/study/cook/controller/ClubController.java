package com.study.cook.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.cook.domain.*;
import com.study.cook.dto.ClubDto;
import com.study.cook.dto.ClubListDto;
import com.study.cook.dto.RecipeSearchCondition;
import com.study.cook.dto.ReservationDto;
import com.study.cook.service.*;
import com.study.cook.util.DateParser;
import com.study.cook.util.JsonMaker;
import com.study.cook.util.MemberFinder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
    private final DateParser dateParser;
    private final MemberFinder memberFinder;
    private final JsonMaker jsonMaker;


    @GetMapping("/list")
    public String list(String categoryName, String title, HttpServletRequest request, Model model, @PageableDefault(size = 8, sort = "regDate", direction = Sort.Direction.ASC) Pageable pageable) {

        RecipeSearchCondition condition = new RecipeSearchCondition();
        condition.setTitle(title);
        condition.setCategoryName(categoryName);

        Page<ClubListDto> list = clubService.findList(condition, pageable);
        List<Category> categories = categoryService.findList();

        model.addAttribute("clubs", list);
        model.addAttribute("maxPage", 4);   // 한 페이지 바 당 보여줄 개수
        model.addAttribute("categories", categories);
//        model.addAttribute("condition", condition);

        return "club/list";
    }

    /**
     * 등록한 리스트
     */
    @GetMapping("/list/created")
    public String createdList(HttpServletRequest request, HttpSession session, Model model, @PageableDefault(size = 8, sort = "regDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Member member = memberFinder.getMember(session);
        List<Category> categories = categoryService.findList();
        Page<ClubListDto> list = clubService.findByMember(member.getId(), pageable);
        model.addAttribute("maxPage", 4);   // 한 페이지 바 당 보여줄 개수
        model.addAttribute("clubs", list);
        model.addAttribute("categories", categories);

        return "club/list";
    }

    /**
     * 참여하는 리스트
     */
    @GetMapping("/list/joined")
    public String joinedList(HttpServletRequest request, HttpSession session, Model model, @PageableDefault(size = 8, sort = "regDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Member member = memberFinder.getMember(session);
        List<Category> categories = categoryService.findList();
        Page<ClubListDto> list = clubService.findByMember(member.getId(), pageable);
        model.addAttribute("maxPage", 4);   // 한 페이지 바 당 보여줄 개수
        model.addAttribute("clubs", list);
        model.addAttribute("categories", categories);

        return "club/list";
    }

    @GetMapping("/participations")
    public String joinList(HttpSession session, Model model, Pageable pageable) {

        Member member = memberFinder.getMember(session);
        Page<ClubListDto> participateClubs = clubService.findByParticipant(member.getId(), pageable);

        model.addAttribute("participateClubs", participateClubs);

        // 참여하는 스터디 리스트 페이지 만들기!
        return "";
    }

    @GetMapping("/{clubId}/detail")
    public String detail(@PathVariable Long clubId, Model model, HttpSession session) {
        Club club = clubService.findOneById(clubId);

        int restCount = club.getMaxCount();
        // 남은 인원수 조회 로직 추가하기
        if (participationService.countByClub(club) != null)
            restCount = (int) (club.getMaxCount() - participationService.countByClub(club));


        Member member = memberService.findOneById(club.getMember().getId());
        // 쿡스터디에 지정된 예약한 요리실 일정
        Optional<List<Reservation>> reservations = reservationService.findByClub(club);
        Category category = categoryService.findOneById(club.getCategory().getId());

        if (reservations.isPresent()) {
            List<ReservationDto> reservationDtos = new ArrayList<>();
            for (Reservation reservation : reservations.get()) {
                LocalDateTime startDateTime = reservation.getStartDateTime();
                LocalDateTime endDateTime = reservation.getEndDateTime();

                String formatDate = dateParser.getFormatDate(startDateTime);
                String formatStartTime = dateParser.getFormatTime(startDateTime);
                String formatEndTime = dateParser.getFormatTime(endDateTime);

                ReservationDto reservationDto = new ReservationDto(reservation.getId(), formatDate, formatStartTime, formatEndTime, reservation.getCookingRoom().getRoomNum());
                reservationDtos.add(reservationDto);
            }
            model.addAttribute("reservationDtos", reservationDtos);
        }

        ClubDto clubDto = new ClubDto(club.getName(), club.getIntroduction(), club.getStatus(), club.getMaxCount(), restCount, club.getPrice(), club.getIngredients());
        Member loginMember = memberFinder.getMember(session);
        Participation participated;
        try {
            participated = participationService.findByClubAndMember(club, loginMember);
        } catch (IllegalArgumentException e) {
            participated = null;
        }

        model.addAttribute("clubDto", clubDto);
        model.addAttribute("memberName", member.getName());
        model.addAttribute("categoryName", category.getName());
        model.addAttribute("clubId", clubId);
//        model.addAttribute("loginMember", loginMember);



        if (member.getId().equals(loginMember.getId())) {    // 로그인한 회원이 쿡스터디 등록한 회원이라면
            return "club/detail-manager";
        }
        model.addAttribute("participated", participated);
        return "club/detail-participant";
    }

    @GetMapping
    public String createForm(Model model, HttpSession session) {
        Optional<List<Reservation>> reservations = reservationService.findByMember(memberFinder.getMember(session));
        List<Category> categories = categoryService.findList();

        if (reservations.isPresent()) {
            List<ReservationDto> reservationDtos = new ArrayList<>();
            for (Reservation reservation : reservations.get()) {

                // 다른 쿡스터디에 지정된 예약은 제외
                Long clubId = null;
                try {
                    clubId = reservation.getClub().getId();
                } catch(NullPointerException e) {
                        continue;
                }

                LocalDateTime startDateTime = reservation.getStartDateTime();
                LocalDateTime endDateTime = reservation.getEndDateTime();

                if (LocalDateTime.now().isAfter(startDateTime))
                    continue;

                String formatDate = dateParser.getFormatDate(startDateTime);
                String formatStartTime = dateParser.getFormatTime(startDateTime);
                String formatEndTime = dateParser.getFormatTime(endDateTime);

                ReservationDto reservationDto = new ReservationDto(reservation.getId(), formatDate, formatStartTime, formatEndTime, reservation.getCookingRoom().getRoomNum());
                reservationDtos.add(reservationDto);

            }
            model.addAttribute("reservationDtos", reservationDtos);
            model.addAttribute("clubForm", new ClubForm());
            model.addAttribute("categories", categories);
        }
        return "club/create-form";
    }


    @PostMapping
    public String create(@Valid @RequestBody ClubForm form, BindingResult result, RedirectAttributes redirectAttributes, HttpSession session) {
        if (result.hasErrors()) {
            return "club/create-form";
        }

        Long clubId = clubService.create(form, session);
        redirectAttributes.addAttribute("clubId", clubId);
        redirectAttributes.addAttribute("status", true);
        return "redirect:/clubs/{clubId}/detail";
    }

    @GetMapping("/{clubId}/edit")
    public String update(@PathVariable Long clubId, Model model) {
        Club club = clubService.findOneById(clubId);
        Optional<List<Reservation>> reservations = reservationService.findByMember(club.getMember());
        List<Category> categories = categoryService.findList();


        ClubForm form = new ClubForm();
        form.setName(club.getName());
        form.setIntroduction(club.getIntroduction());
        form.setMaxCount(club.getMaxCount());
        form.setIngredients(club.getIngredients());
        form.setMemberId(club.getMember().getId());
        form.setCategoryId(club.getCategory().getId());

        if (reservations.isPresent()) {
            List<ReservationDto> reservationDtos = new ArrayList<>();
            for (Reservation reservation : reservations.get()) {

                Long id = null;
                // 다른 쿡스터디에 지정된 예약은 제외
                try {
                    id = reservation.getClub().getId();
                } catch(NullPointerException e) {
                    continue;
                }
                if (!(reservation.getClub().getId().equals(id)))
                    continue;

                LocalDateTime startDateTime = reservation.getStartDateTime();
                LocalDateTime endDateTime = reservation.getEndDateTime();

                // 현재 날짜보다 앞이면 제외
                if (LocalDateTime.now().isAfter(startDateTime))
                    continue;

                String formatDate = dateParser.getFormatDate(startDateTime);
                String formatStartTime = dateParser.getFormatTime(startDateTime);
                String formatEndTime = dateParser.getFormatTime(endDateTime);

                ReservationDto reservationDto = new ReservationDto(reservation.getId(), formatDate, formatStartTime, formatEndTime, reservation.getCookingRoom().getRoomNum());
                reservationDtos.add(reservationDto);
            }
            // 체크된 예약 요리실
            List<Reservation> checkedReservations = reservationService.findByClub(club).orElse(null);
            if (checkedReservations != null)
                form.setReservationIds(checkedReservations.stream().map((c) -> c.getId()).collect(Collectors.toList()));

            model.addAttribute("reservationDtos", reservationDtos);
        }

        model.addAttribute("clubForm", form);
        model.addAttribute("reservations", reservations);
        model.addAttribute("categories", categories);
        model.addAttribute("clubId", clubId);
        return "club/update-form";
    }

    @ResponseBody
    @PutMapping("/{clubId}")
    public String update(@PathVariable Long clubId, @RequestBody @Valid ClubForm form, BindingResult result, RedirectAttributes redirectAttributes, Model model) throws JsonProcessingException {


        if (result.hasErrors()) {
            return "club/update-form";
        }
        Map<String, Object> results = new HashMap<>();

        try {
            clubService.update(clubId, form);
        } catch (IllegalArgumentException e) {
            results.put("SUCCESS", false);
            results.put("msg", "수정에 실패했습니다.");
            results.put("url", "/clubs/" + clubId + "/edit");

            return jsonMaker.getJson(results);
        }

        results.put("SUCCESS", true);
        results.put("msg", "수정하였습니다!");
        results.put("url", "/clubs/" + clubId + "/detail");

        // json으로 변환
        String json = jsonMaker.getJson(results);

        return json;
    }

    @DeleteMapping("/{clubId}")
    public String delete(@PathVariable Long clubId) {

        clubService.delete(clubId);

        return "redirect:/clubs/list";
    }

}
