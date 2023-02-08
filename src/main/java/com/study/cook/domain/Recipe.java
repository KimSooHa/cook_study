package com.study.cook.domain;

import com.study.cook.controller.RecipeForm;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "introduction", "img", "ingredients", "cookingTime", "servings", "likes", "regDate"})
public class Recipe {

    @Id
    @GeneratedValue
    @Column(name = "recipe_id")
    private Long id;

    private String introduction;
    private String img;
    private String ingredients; // 재료
    private int cookingTime;    // 요리시간
    private int servings;   // 인분
    private int likeCount;  // 좋아요 수
    private LocalDateTime regDate;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "recipe")
    private List<RecipeField> recipeFields = new ArrayList<>();

    @OneToMany(mappedBy = "recipe")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "recipe")
    private List<Like> likes = new ArrayList<>();

    public Recipe(String introduction, String img, String ingredients, int cookingTime, int servings) {
        this.introduction = introduction;
        this.img = img;
        this.ingredients = ingredients;
        this.cookingTime = cookingTime;
        this.servings = servings;
    }

    //== 연관관계 메서드==//
    // 양방향으로 연관관계의 값을 세팅
    public void setMember(Member member) {
        this.member = this.member;
        member.getRecipes().add(this);
    }

    public void setCategory(Category category) {
        this.category = category;
        category.getRecipes().add(this);
    }

    public void addRecipeField(RecipeField recipeField) {
        recipeFields.add(recipeField);
        recipeField.setRecipe(this);
    }

    //==생성 메서드==//
    public static Recipe createRecipe(Recipe recipe, Member member, Category category, RecipeField... recipeFields) {

        recipe.setRegDate(LocalDateTime.now());

        recipe.setMember(member);
        recipe.setCategory(category);

        for (RecipeField recipeField : recipeFields) {
            recipe.addRecipeField(recipeField);
        }

        return recipe;

    }


}
