package com.study.cook.repository;

import com.study.cook.dto.RecipeListDto;
import com.study.cook.dto.SearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RecipeRepositoryCustom {

//    List<Recipe> findByTitle(String title);

    Page<RecipeListDto> findByMemberId(Long memberId, SearchCondition condition, Pageable pageable);

    Page<RecipeListDto> findList(SearchCondition condition, Pageable pageable);
}
