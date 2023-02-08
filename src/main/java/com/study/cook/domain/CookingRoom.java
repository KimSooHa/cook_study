package com.study.cook.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "roomNum"})
public class CookingRoom {

    @Id
    @GeneratedValue
    @Column(name = "cooking_room_id")
    private Long id;

    private int roomNum;

    public CookingRoom(int roomNum) {
        this.roomNum = roomNum;
    }

    @OneToMany(mappedBy = "cookingRoom")
    private List<CookingRoomSchedule> cookingRoomSchedules = new ArrayList<>();

    @OneToMany(mappedBy = "cookingRoom")
    private List<Reservation> reservations = new ArrayList<>();

}
