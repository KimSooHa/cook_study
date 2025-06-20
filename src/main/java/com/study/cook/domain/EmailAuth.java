package com.study.cook.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "email", "authCode", "regDate"})
public class EmailAuth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "email_auth_id")
    private Long id;

    @Column(nullable = false, length = 320)
    private String email;

    @Column(nullable = false, length = 10)
    private String authCode;

    @Column(nullable = false)
    private LocalDateTime regDate;

    private boolean isVerified;

    public EmailAuth(String email) {
        this.email = email;
        this.regDate = LocalDateTime.now();
    }
}