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
@ToString(of = {"id", "img", "content"})
public class RecipeField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_field_id")
    private Long id;

    @NotNull
    @OneToOne(cascade = CascadeType.ALL, fetch=LAZY)
    @JoinColumn(name = "photo_id")
    private Photo photo;
    @NotNull
    @Column(length = 200)
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

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
        recipe.getRecipeFields().add(this);
    }

    public RecipeField(Photo photo, String content) {
        this.photo = photo;
        this.content = content;
    }

    //==생성 메서드==//
    public static RecipeField createRecipeField(RecipeField recipeField, Member member, Recipe recipe) {

        recipeField.setRegDate(LocalDateTime.now());

        recipeField.setMember(member);

        recipeField.setRecipe(recipe);

        return recipeField;

    }
}
