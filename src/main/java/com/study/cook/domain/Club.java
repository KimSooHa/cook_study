package com.study.cook.domain;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.study.cook.domain.ClubStatus.POS;
import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "name", "introduction", "status", "maxCount", "price", "regDate"})
public class Club {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "club_id")
    private Long id;
    @NotNull
    @Column(length = 45)
    private String name;

    @NotNull
    @Column(length = 100)
    private String introduction;

    @NotNull
    @Column(length = 10)
    @Enumerated(EnumType.STRING)
    private ClubStatus status;

    @NotNull
    @Column(columnDefinition = "TINYINT")
    private int maxCount;

    @NotNull
    @Column(columnDefinition = "SMALLINT")
    private int price;

    @NotNull
    @Column(length = 200)
    private String ingredients;

    @NotNull
    private LocalDateTime regDate;

    @NotNull
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @NotNull
    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL)
    private List<Participation> participations = new ArrayList<>();

    @NotNull
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "club", cascade = CascadeType.PERSIST)
    private List<Reservation> reservations = new ArrayList<>();


    public Club(String name, String introduction, int maxCount, String ingredients) {
        this.name = name;
        this.introduction = introduction;
        this.maxCount = maxCount;
        this.ingredients = ingredients;
    }

    //==연관관계 메서드==//
    public void setMember(Member member) {
        this.member = member;
        member.getClubs().add(this);
    }

    public void setCategory(Category category) {
        this.category = category;
        category.getClubs().add(this);
    }

    public void addReservation(Reservation reservation) {
        reservations.add(reservation);
        reservation.setClub(this);
    }

    public static Club createClub(Club club, Member member, Category category, List<Reservation> reservations) {

        club.setStatus(POS);
        club.setPrice(12000);
        club.setRegDate(LocalDateTime.now());

        club.setMember(member);
        club.setCategory(category);

        for (Reservation reservation : reservations) {
            club.addReservation(reservation);
        }

        return club;
    }


}
