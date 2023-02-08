package com.study.cook.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "startTime", "endTime", "useDate", "regDate"})
public class Reservation {

    @Id
    @GeneratedValue
    @Column(name = "reservation_id")
    private Long id;

    private String startTime;
    private String endTime;
    private String useDate;
    private LocalDateTime regDate;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "cooking_room_id")
    private CookingRoom cookingRoom;

    public Reservation(String startTime, String endTime, String useDate) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.useDate = useDate;
    }

    public void setMember(Member member) {
        this.member = member;
        member.getReservations().add(this);
    }

    public void setCookingRoom(CookingRoom cookingRoom) {
        this.cookingRoom = cookingRoom;
        cookingRoom.getReservations().add(this);
    }

    public static Reservation createReservation(Reservation reservation, Member member, CookingRoom cookingRoom) {

        reservation.setRegDate(LocalDateTime.now());

        reservation.setMember(member);
        reservation.setCookingRoom(cookingRoom);

        return reservation;
    }

}
