package com.study.cook.domain;

import com.study.cook.enums.Role;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import java.time.LocalDateTime;

import static com.study.cook.enums.Role.MANAGER;
import static com.study.cook.enums.Role.PARTICIPANT;
import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "role", "regDate"})
public class Participation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participation_id")
    private Long id;

    @NotNull
    @Column(length = 15)
    @Enumerated(EnumType.STRING)
    private Role role;   // MANAGER[운영자], PARTICIPANT[참여자]
    @NotNull
    private LocalDateTime regDate;

    @NotNull
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @NotNull
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "club_id")
    private Club club;



    public void setMember(Member member) {
        this.member = member;
        member.getParticipations().add(this);
    }

    public void setClub(Club club) {
        this.club = club;
        club.getParticipations().add(this);
    }

    public static Participation createParticipation(Member member, Club club) {
        Participation participation = new Participation();

        if(member.getId() == club.getMember().getId())
            participation.setRole(MANAGER);
        else
            participation.setRole(PARTICIPANT);


        participation.setRegDate(LocalDateTime.now());

        participation.setMember(member);
        participation.setClub(club);

        return participation;
    }


}
