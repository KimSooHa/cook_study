package com.study.cook.domain;

import lombok.*;

import javax.persistence.*;

import java.time.LocalDateTime;

import static com.study.cook.domain.Role.MANAGER;
import static com.study.cook.domain.Role.PARTICIPANT;
import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "role", "useDate"})
public class Participation {

    @Id
    @GeneratedValue
    @Column(name = "participation_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private Role role;   // MANAGER[운영자], PARTICIPANT[참여자]
    private LocalDateTime regDate;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "group_id")
    private Group group;



    public void setMember(Member member) {
        this.member = member;
        member.getParticipations().add(this);
    }

    public void setGroup(Group group) {
        this.group = group;
        group.getParticipations().add(this);
    }

    public static Participation createParticipation(Member member, Group group) {
        Participation participation = new Participation();

        if(member.getId() == group.getMember().getId()) {
            participation.setRole(MANAGER);
        }
        else {
            participation.setRole(PARTICIPANT);
        }

        participation.setRegDate(LocalDateTime.now());

        participation.setMember(member);
        participation.setGroup(group);

        return participation;
    }


}
