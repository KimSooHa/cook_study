package com.study.cook.domain;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "title", "introduction", "img", "ingredients", "cookingTime", "servings", "likes", "regDate"})
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_id")
    private Long id;

    @NotNull
    @Column(length = 20)
    private String title;

    @Column(length = 200)
    private String introduction;

    @NotNull
    @OneToOne(cascade = CascadeType.ALL, fetch=LAZY)
    @JoinColumn(name = "photo_id")
    private Photo photo;

    @Column(length = 200)
    @NotNull
    private String ingredients; // 재료

    @NotNull
    @Column(columnDefinition = "TINYINT", length = 1)
    private int cookingTime;    // 요리시간

    @NotNull
    @Column(columnDefinition = "TINYINT", length = 1)
    private int servings;   // 인분
    @NotNull
    private LocalDateTime regDate;

    @NotNull
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @NotNull
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @NotNull
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL)
    private List<RecipeField> recipeFields = new ArrayList<>();

    @OneToMany(mappedBy = "recipe")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "recipe")
    private List<Heart> hearts = new ArrayList<>();

    public Recipe(String introduction, Photo photo, String ingredients, int cookingTime, int servings) {
        this.introduction = introduction;
        this.photo = photo;
        this.ingredients = ingredients;
        this.cookingTime = cookingTime;
        this.servings = servings;
    }

    //== 연관관계 메서드==//
    // 양방향으로 연관관계의 값을 세팅
    public void setMember(Member member) {
        this.member = member;
        member.getRecipes().add(this);
    }

    public void setCategory(Category category) {
        this.category = category;
        category.getRecipes().add(this);
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
        photo.setRecipe(this);
    }

    public void addRecipeField(RecipeField recipeField) {
        recipeFields.add(recipeField);
        recipeField.setRecipe(this);
    }

    //==생성 메서드==//
    public static Recipe createRecipe(Recipe recipe, Member member, Category category, Photo photo) {

        recipe.setRegDate(LocalDateTime.now());

        recipe.setMember(member);
        recipe.setCategory(category);
        recipe.setPhoto(photo);

//        for (RecipeField recipeField : recipeFields) {
//            recipe.addRecipeField(recipeField);
//        }

        return recipe;

    }


}
