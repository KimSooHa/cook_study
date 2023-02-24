package com.study.cook.repository;

import com.study.cook.domain.RecipeField;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecipeFieldRepository extends JpaRepository<RecipeField, Long> {


    //    Member findByEmailAndPhoneNum(String email, String phoneNum);
//
//    Member findByLoginIdAndEmail(String loginId, String email);
    Optional<List<RecipeField>> findByRecipe(Long recipeId);

}
