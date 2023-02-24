package com.study.cook.repository;

import com.study.cook.domain.Member;
import com.study.cook.domain.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecipeRepository extends JpaRepository<Recipe, Long>, RecipeRepositoryCustom {


//    Member findByEmailAndPhoneNum(String email, String phoneNum);
//
//    Member findByLoginIdAndEmail(String loginId, String email);


}
