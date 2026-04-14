package com.study.cook.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import static javax.persistence.FetchType.LAZY;

@Data
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"uploadFileName", "storeFileName"})
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "photo_id")
    private Long id;

    @NotNull
    private String uploadFileName;  // 고객이 업로드한 파일명
    @NotNull
    private String storeFileName;   // 서버 내부에서 관리하는 파일명

    @OneToOne(mappedBy = "photo", fetch = LAZY)
    @JsonIgnore
    private Recipe recipe;

    @OneToOne(mappedBy = "photo", fetch = LAZY)
    private RecipeField recipeField;

    public Photo(String uploadFileName, String storeFileName) {
        this.uploadFileName = uploadFileName;
        this.storeFileName = storeFileName;
    }

    public void setRecipeField(RecipeField recipeField) {
        this.recipeField = recipeField;
        recipeField.setPhoto(this);
    }

    public static Photo createPhoto(Photo photo, RecipeField recipeField) {
        photo.setRecipeField(recipeField);
        return photo;
    }
}