package com.study.cook.domain;

import lombok.*;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "img", "content"})
public class RecipeField {

    @Id
    @GeneratedValue
    @Column(name = "recipe_form_id")
    private Long id;

    private String img;
    private String content;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    public RecipeField(String img, String content) {
        this.img = img;
        this.content = content;
    }
}
