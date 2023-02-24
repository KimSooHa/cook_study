package com.study.cook;

import com.study.cook.controller.ReservationController;
import com.study.cook.controller.ReservationForm;
import com.study.cook.domain.Category;
import com.study.cook.domain.CookingRoom;
import com.study.cook.domain.Member;
import com.study.cook.domain.Schedule;
import com.study.cook.repository.MemberRepository;
import com.study.cook.service.*;
import com.study.cook.util.DateParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Profile("local")
@Component
@RequiredArgsConstructor
@Slf4j
public class InitDb {

    private final EntityManager em;

    private final ScheduleService scheduleService;
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final CategoryService categoryService;

    private final CookingRoomService cookingRoomService;
    private final ReservationService reservationService;
    private ReservationController reservationController;
    private DateParser dateParser;

//    @PostConstruct
    @Transactional
    public void init() {
        // member
//        for (int i = 0; i < 20; i++) {
//            Member member = new Member("멤버", "member" + i, "member1234**", "member" + i + "@email.com", "010-1234-1234");
//            memberService.join(member);
//        }
        Member member = new Member("쿠키", "cookie", "cookie123*", "cookie123@naver.com", "010-1111-2222");
        memberRepository.save(member);
//        MemberForm memberForm = new MemberForm();
//        memberForm.setName("쿠키");
//        memberForm.setLoginId("cookie");
//        memberForm.setPwd("cookie123*");
//        memberForm.setEmail("cookie123@naver.com");
//        memberForm.setPhoneNum("010-1111-2222");
//        Long memberId = memberService.join(memberForm);

        // schedule
        List<Schedule> schedules = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            LocalTime startTime = LocalTime.of(10 + i, 0);
            LocalTime endTime = LocalTime.of(11 + i, 0);
            Schedule schedule = new Schedule(startTime, endTime);
            schedules.add(schedule);
        }

        // category
        Category category1 = new Category("한식");
        Category category2 = new Category("양식");
        Category category3 = new Category("일식");
        Category category4 = new Category("중식");
        Category category5 = new Category("디저트");

        categoryService.create(category1);
        categoryService.create(category2);
        categoryService.create(category3);
        categoryService.create(category4);
        categoryService.create(category5);

        // CookingRoom
        CookingRoom cookingRoom1 = new CookingRoom(10, 101);
        CookingRoom cookingRoom2 = new CookingRoom(10, 102);
        CookingRoom cookingRoom3 = new CookingRoom(10, 103);
        CookingRoom cookingRoom4 = new CookingRoom(10, 104);
        CookingRoom cookingRoom5 = new CookingRoom(10, 105);


        cookingRoomService.create(cookingRoom1, schedules);
        cookingRoomService.create(cookingRoom2, schedules);
        cookingRoomService.create(cookingRoom3, schedules);
        cookingRoomService.create(cookingRoom4, schedules);
        cookingRoomService.create(cookingRoom5, schedules);

//        // 전체 요리실에 시간 추가
//        LocalTime startTime = LocalTime.of(18, 0);
//        LocalTime endTime = LocalTime.of(19, 0);

//        scheduleService.createAndMatchAll(startTime, endTime);
//        scheduleService.deleteAllByTime(startTime);

        // 요리실 예약
//        Reservation reservation = new Reservation(LocalDateTime.of(2023, 2, 28, 10, 0), LocalDateTime.of(2023, 2, 28, 11, 0));
//        ReservationForm form = new ReservationForm();
//        LocalDate date = LocalDate.of(2023, 02, 28);
//        String formatDate = date.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
//
//        form.setDate(formatDate);
//        List<List<String>> times = new ArrayList<>();
//        times.add(new ArrayList<>());
//        times.get(0).add("10:00");
//        times.get(0).add("11:00");
//        form.setTimes(times);
//        reservationService.create(form, member);






    }


}
