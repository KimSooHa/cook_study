package com.study.cook.domain;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "startTime", "endTime"})
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long id;

    @NotNull
    private LocalTime startTime;
    @NotNull
    private LocalTime endTime;

    @ManyToOne
    @JoinColumn(name = "cooking_room_id")
    private CookingRoom cookingRoom;

    public Schedule(LocalTime startTime, LocalTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
