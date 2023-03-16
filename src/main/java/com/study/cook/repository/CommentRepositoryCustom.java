package com.study.cook.repository;

import com.study.cook.dto.CommentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentRepositoryCustom {

    Page<CommentDto> findList(Long recipeId, Pageable pageable);
}
