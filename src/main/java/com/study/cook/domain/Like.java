package com.study.cook.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "role", "useDate"})
public class Like {

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    //== 연관관계 메서드==//
    public void setMember(Member member) {
        this.member = member;
        member.getLikes().add(this);
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
        recipe.getLikes().add(this);
    }

    public static Like pressLike(Member member, Recipe recipe) {
        Like like = new Like();

        like.setMember(member);
        like.setRecipe(recipe);

        return like;
    }
}
