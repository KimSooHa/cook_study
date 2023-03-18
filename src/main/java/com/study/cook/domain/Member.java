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
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // 생성자 함수를 protected로 설정
@ToString(of = {"id", "name", "loginId", "pwd", "email", "phoneNum", "regDate"})
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @NotNull
    @Column(length = 18)
    private String name;

    @NotNull
    @Column(length = 10)
    private String loginId;

    @NotNull
    @Column(length = 16)
    private String pwd;

    @NotNull
    @Column(length = 320)
    private String email;

    @NotNull
    @Column(length = 13)
    private String phoneNum;

    @NotNull
    private LocalDateTime regDate;

    public Member(String name, String loginId, String pwd, String email, String phoneNum) {
        this.name = name;
        this.loginId = loginId;
        this.pwd = pwd;
        this.email = email;
        this.phoneNum = phoneNum;
        this.regDate = LocalDateTime.now();
    }

    @OneToMany(mappedBy = "member")
    private List<Recipe> recipes = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Reservation> reservations = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Participation> participations = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Club> clubs = new ArrayList<>();
    
}
