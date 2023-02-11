package com.study.cook.domain;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "maxCount", "roomNum"})
public class CookingRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cooking_room_id")
    private Long id;

    @NotNull
    @Column(columnDefinition = "TINYINT")
    private int maxCount;   // 최대인원
    @NotNull
    @Column(columnDefinition = "TINYINT")
    private int roomNum;    // 요리실 번호

    public CookingRoom(int roomNum) {
        this.roomNum = roomNum;
    }
    @NotNull
    @OneToMany(mappedBy = "cookingRoom")
    private List<Schedule> schedules = new ArrayList<>();

    @OneToMany(mappedBy = "cookingRoom")
    private List<Reservation> reservations = new ArrayList<>();

    //==연관관계 메서드==//
    public void addSchedule(Schedule schedule) {
        schedules.add(schedule);
        schedule.setCookingRoom(this);
    }

    //==생성 메서드==//
    public static CookingRoom createCookingRoom(CookingRoom cookingRoom, Schedule... schedules) {

        for (Schedule schedule : schedules) {
            cookingRoom.addSchedule(schedule);
        }

        return cookingRoom;
    }
}
