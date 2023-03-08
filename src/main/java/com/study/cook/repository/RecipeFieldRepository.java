package com.study.cook.repository;

import com.study.cook.domain.RecipeField;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecipeFieldRepository extends JpaRepository<RecipeField, Long> {

    Optional<List<RecipeField>> findByRecipeId(Long recipeId);

}
