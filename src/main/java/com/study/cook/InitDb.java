package com.study.cook;

import com.study.cook.domain.Category;
import com.study.cook.domain.CookingRoom;
import com.study.cook.domain.Member;
import com.study.cook.domain.Schedule;
import com.study.cook.service.CategoryService;
import com.study.cook.service.CookingRoomService;
import com.study.cook.service.MemberService;
import com.study.cook.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import java.time.LocalTime;
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
    private final CategoryService categoryService;

    private final CookingRoomService cookingRoomService;

    @PostConstruct
    @Transactional
    public void init() {
        // member
        for (int i = 0; i < 20; i++) {
            Member member = new Member("멤버", "member" + i, "member1234**", "member" + i + "@email.com", "010-1234-1234");
            memberService.join(member);
        }

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

    }

}
