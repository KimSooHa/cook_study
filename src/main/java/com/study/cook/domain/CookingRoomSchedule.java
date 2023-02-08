package com.study.cook.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "useDate"})
public class CookingRoomSchedule {

    @Id
    @GeneratedValue
    @Column(name = "cooking_room_schedule_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private CookingRoomStatus status;   // POS[가능], WAIT[대기], COMP[가능]
    private String useDate;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "cooking_room_id")
    private CookingRoom cookingRoom;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    public CookingRoomSchedule(CookingRoomStatus status, String useDate) {
        this.status = status;
        this.useDate = useDate;
    }

    public void setCookingRoom(CookingRoom cookingRoom) {
        this.cookingRoom = cookingRoom;
        cookingRoom.getCookingRoomSchedules().add(this);
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
        schedule.getCookingRoomSchedules().add(this);
    }

    public static CookingRoomSchedule createCookingRoomSchedule(CookingRoomSchedule roomSchedule, CookingRoom cookingRoom, Schedule schedule) {
        roomSchedule.setCookingRoom(cookingRoom);
        roomSchedule.setSchedule(schedule);

        return roomSchedule;
    }
}
