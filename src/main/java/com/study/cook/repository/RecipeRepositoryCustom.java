package com.study.cook.repository;

import com.study.cook.dto.RecipeListDto;
import com.study.cook.dto.SearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RecipeRepositoryCustom {

//    List<Recipe> findByTitle(String title);
    Page<RecipeListDto> findAll(SearchCondition condition, Pageable pageable);

    Page<RecipeListDto> findByMemberId(Long memberId, Pageable pageable);

}
