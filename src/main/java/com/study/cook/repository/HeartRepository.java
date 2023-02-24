package com.study.cook.repository;

import com.study.cook.domain.Heart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HeartRepository extends JpaRepository<Heart, Long> {


    Long countByRecipe(Long recipeId);


}
