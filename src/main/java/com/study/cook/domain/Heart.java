package com.study.cook.domain;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Heart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "heart_id")
    private Long id;

    @NotNull
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @NotNull
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    //== 연관관계 메서드==//
    public void setMember(Member member) {
        this.member = member;
        member.getHearts().add(this);
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
        recipe.getHearts().add(this);
    }

    public static Heart pressHeart(Member member, Recipe recipe) {
        Heart heart = new Heart();

        heart.setMember(member);
        heart.setRecipe(recipe);

        return heart;
    }
}
