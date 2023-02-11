package com.study.cook.domain;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "content", "regDate"})
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @NotNull
    @Column(length = 300)
    private String content;

    @NotNull
    private LocalDateTime regDate;

    @NotNull
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @NotNull
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    public Comment(String content) {
        this.content = content;
        this.regDate = LocalDateTime.now();
    }

    //== 연관관계 메서드==//
    // 양방향으로 연관관계의 값을 세팅
    public void setMember(Member member) {
        this.member = member;
        member.getComments().add(this);
    }

    public static Comment createComment(Comment comment, String content, Member member, Recipe recipe) {

        comment.setContent(content);
        comment.setMember(member);
        comment.setRecipe(recipe);

        return comment;
    }


}
