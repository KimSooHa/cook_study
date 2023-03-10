package com.study.cook.domain;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "startDateTime", "endDateTime", "regDate"})
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long id;

    @NotNull
    private LocalDateTime startDateTime;

    @NotNull
    private LocalDateTime endDateTime;

    @NotNull
    private LocalDateTime regDate;

    @NotNull
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @NotNull
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "cooking_room_id")
    private CookingRoom cookingRoom;

    @ManyToOne(fetch = LAZY)
    private Club club;


    public Reservation(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.regDate = LocalDateTime.now();
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
