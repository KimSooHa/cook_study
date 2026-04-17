package com.study.cook.repository;

import com.study.cook.dto.CommentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CommentRepositoryCustom {

//    Page<CommentDto> findList(Long recipeId, Pageable pageable);

    Slice<CommentDto> findList(Long recipeId, Long lastId, int size);

    long countByRecipe(Long recipeId);
}
