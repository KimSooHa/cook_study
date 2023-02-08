package com.study.cook.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.study.cook.domain.GroupStatus.POS;
import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "name", "introduction", "status", "maxCount", "price", "regDate"})
public class Group {

    @Id
    @GeneratedValue
    @Column(name = "group_id")
    private Long id;

    private String name;
    private String introduction;

    @Enumerated(EnumType.STRING)
    private GroupStatus status;
    private int maxCount;
    private int price;
    private String ingredients;
    private LocalDateTime regDate;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "group")
    private List<Participation> participations = new ArrayList<>();

    public Group(String name, String introduction, int maxCount, String ingredients) {
        this.name = name;
        this.introduction = introduction;
        this.maxCount = maxCount;
        this.ingredients = ingredients;
    }

    //==연관관계 메서드==//
    public void setMember(Member member) {
        this.member = member;
        member.getGroups().add(this);
    }

    public static Group createGroup(Group group, Member member) {

        group.setStatus(POS);
        group.setPrice(12000);
        group.setRegDate(LocalDateTime.now());

        group.setMember(member);

        return group;
    }


}
