package com.study.cook.controller;

import com.study.cook.domain.*;
import com.study.cook.dto.ClubDto;
import com.study.cook.dto.ClubListDto;
import com.study.cook.dto.SearchCondition;
import com.study.cook.dto.ReservationDto;
import com.study.cook.service.*;
import com.study.cook.util.DateParser;
import com.study.cook.util.MemberFinder;
import com.study.cook.util.ResultVO;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.springframework.data.domain.Sort.Direction.DESC;

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


    @GetMapping("/list")
    public String list(String categoryName, @RequestParam(defaultValue = "") String title, Model model, @PageableDefault(size = 8, sort = "regDate", direction = DESC) Pageable pageable) {

        SearchCondition condition = new SearchCondition();
        if (title != null || title != "")
            condition.setTitle(title);
        if (categoryName != null || categoryName != "")
            condition.setCategoryName(categoryName);

        Page<ClubListDto> list = clubService.findList(condition, pageable);
        List<Category> categories = categoryService.findList();

        model.addAttribute("list", list);
        model.addAttribute("maxPage", 4);   // 한 페이지 바 당 보여줄 개수
        model.addAttribute("categories", categories);
        model.addAttribute("title", title);
        model.addAttribute("categoryName", categoryName);

        return "club/list";
    }

    /**
     * 등록한 리스트
     */
    @GetMapping("/list/created")
    public String createdList(String categoryName, @RequestParam(defaultValue = "") String title, HttpSession session, Model model, @PageableDefault(size = 8, sort = "regDate", direction = Sort.Direction.ASC) Pageable pageable) {

        SearchCondition condition = new SearchCondition();
        if (title != null || title != "")
            condition.setTitle(title);
        if (categoryName != null || categoryName != "")
            condition.setCategoryName(categoryName);

        Member member = memberFinder.getMember(session);
        List<Category> categories = categoryService.findList();
        Page<ClubListDto> list = clubService.findByMember(member.getId(), condition, pageable);

        model.addAttribute("maxPage", 4);
        model.addAttribute("list", list);
        model.addAttribute("categories", categories);
        model.addAttribute("title", title);
        model.addAttribute("categoryName", categoryName);

        return "club/created-list";
    }

    /**
     * 참여하는 리스트
     */
    @GetMapping("/list/joined")
    public String joinedList(String categoryName, @RequestParam(defaultValue = "") String title, HttpSession session, Model model, @PageableDefault(size = 8, sort = "regDate", direction = DESC) Pageable pageable) {

        SearchCondition condition = new SearchCondition();
        if (title != null || title != "")
            condition.setTitle(title);
        if (categoryName != null || categoryName != "")
            condition.setCategoryName(categoryName);

        Member member = memberFinder.getMember(session);
        List<Category> categories = categoryService.findList();
        Page<ClubListDto> list = clubService.findByParticipant(member.getId(), condition, pageable);
        model.addAttribute("maxPage", 4);
        model.addAttribute("list", list);
        model.addAttribute("categories", categories);
        model.addAttribute("title", title);
        model.addAttribute("categoryName", categoryName);

        return "club/joined-list";
    }


    @GetMapping("/{clubId}/detail")
    public String detail(@PathVariable Long clubId, @RequestParam(defaultValue = "/clubs/list") String redirectURL, Model model, HttpSession session) {
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
            List<ReservationDto> reservationDtos = reservations.get().stream().map(m -> {
                LocalDateTime startDateTime = m.getStartDateTime();
                LocalDateTime endDateTime = m.getEndDateTime();

                String formatDate = dateParser.getFormatDate(startDateTime);
                String formatStartTime = dateParser.getFormatTime(startDateTime);
                String formatEndTime = dateParser.getFormatTime(endDateTime);

                return new ReservationDto(m.getId(), formatDate, formatStartTime, formatEndTime, m.getCookingRoom().getRoomNum());
            }).collect(toList());
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
        model.addAttribute("memberLoginId", member.getLoginId());
        model.addAttribute("categoryName", category.getName());
        model.addAttribute("clubId", clubId);
        model.addAttribute("redirectURL", redirectURL);

        if (member.getId().equals(loginMember.getId())) {    // 로그인한 회원이 쿡스터디 등록한 회원이라면
            return "club/detail-manager";
        }
        model.addAttribute("participated", participated);
        return "club/detail-participant";
    }

    @GetMapping
    public String createForm(Model model, HttpSession session) {
        Optional<List<Reservation>> reservations = reservationService.findByMemberAndDateGt(memberFinder.getMember(session), LocalDateTime.now());
        List<Category> categories = categoryService.findList();
        log.info("reservations={}", reservations.get().size());
        if (reservations.isPresent()) {
            List<ReservationDto> reservationDtos = new ArrayList<>();
            for (Reservation reservation : reservations.get()) {

                // 다른 쿡스터디에 지정된 예약은 제외
                Long clubId = null;
                try {
                    clubId = reservation.getClub().getId();
                } catch (NullPointerException e) {
                    makeReservationListDto(reservationDtos, reservation);
                }
            }
            model.addAttribute("reservationDtos", reservationDtos);
        }
        model.addAttribute("clubForm", new ClubForm());
        model.addAttribute("categories", categories);
        return "club/create-form";
    }


    @ResponseBody
    @PostMapping
    public ResultVO create(@Valid @RequestBody ClubForm form, BindingResult result, RedirectAttributes redirectAttributes, HttpSession session) {
        if (result.hasErrors()) {
            return new ResultVO("등록에 실패했습니다.", "/clubs", false);
        }

        Long clubId = clubService.create(form, session);
        return new ResultVO("등록하였습니다.", "/clubs/" + clubId + "/detail", true);
    }

    @GetMapping("/{clubId}/edit")
    public String update(@PathVariable Long clubId, Model model) {
        Club club = clubService.findOneById(clubId);
        Optional<List<Reservation>> reservations = reservationService.findByMemberAndDateGt(club.getMember(), LocalDateTime.now());
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
                // 체크된 예약 요리실
                Optional<List<Reservation>> checkedReservations = reservationService.findByClub(club);
                if (checkedReservations.isPresent()) {
                    form.setReservationIds(checkedReservations.get().stream().map((c) -> c.getId()).collect(toList()));
                    for (Reservation cr : checkedReservations.get()) {
                        // 기존에 선택한 예약 요리실
                        if (cr.getId().equals(reservation.getId())) {
                            makeReservationListDto(reservationDtos, reservation);
                            continue;
                        }
                    }
                }

                // 다른 쿡스터디에 지정된 예약은 제외
                try {
                    reservation.getClub().getId();
                } catch (NullPointerException e) {
                    makeReservationListDto(reservationDtos, reservation);
                }
            }
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
    public ResultVO update(@PathVariable Long clubId, @RequestBody @Valid ClubForm form, BindingResult result) {

        if (result.hasErrors())
            return new ResultVO("수정에 실패했습니다.", "/clubs/" + clubId + "/edit", false);


        try {
            clubService.update(clubId, form);
        } catch (IllegalArgumentException e) {
            return new ResultVO("수정에 실패했습니다.", "/clubs/" + clubId + "/edit", false);
        }

        return new ResultVO("수정하였습니다!", "/clubs/" + clubId + "/detail", true);
    }

    @DeleteMapping("/{clubId}")
    public String delete(@PathVariable Long clubId, RedirectAttributes redirectAttributes, @RequestParam(defaultValue = "/clubs/list") String redirectURL) {
        clubService.delete(clubId);
        redirectAttributes.addFlashAttribute("msg", "삭제되었습니다!");
        log.info("redirectURL={}", redirectURL);
        return "redirect:" + redirectURL;
    }

    private void makeReservationListDto(List<ReservationDto> reservationDtos, Reservation reservation) {
        String formatDate = dateParser.getFormatDate(reservation.getStartDateTime());
        String formatStartTime = dateParser.getFormatTime(reservation.getStartDateTime());
        String formatEndTime = dateParser.getFormatTime(reservation.getEndDateTime());

        ReservationDto reservationDto = new ReservationDto(reservation.getId(), formatDate, formatStartTime, formatEndTime, reservation.getCookingRoom().getRoomNum());
        reservationDtos.add(reservationDto);
    }
}
