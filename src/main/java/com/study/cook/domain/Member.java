package com.study.cook.domain;

import lombok.*;

import javax.persistence.*;
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
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String name;
    private String nickname;
    private String loginId;
    private String pwd;
    private String email;
    private String phoneNum;
    private LocalDateTime regDate;

    public Member(String name, String nickname, String loginId, String pwd, String email, String phoneNum) {
        this.name = name;
        this.nickname = nickname;
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
    private List<Like> likes = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Group> groups = new ArrayList<>();
    
}
