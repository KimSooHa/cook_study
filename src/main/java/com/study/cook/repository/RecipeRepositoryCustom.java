package com.study.cook.repository;

import com.study.cook.dto.RecipeListDto;
import com.study.cook.dto.RecipeSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RecipeRepositoryCustom {

//    List<Recipe> findByTitle(String title);
    Page<RecipeListDto> findAll(RecipeSearchCondition condition, Pageable pageable);

    Page<RecipeListDto> findByMemberId(Long memberId, Pageable pageable);

}
