package com.study.cook.repository;

import com.study.cook.dto.ClubListDto;
import com.study.cook.dto.RecipeSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClubRepositoryCustom {

//    Page<ClubListDto> findAll(RecipeSearchCondition condition, Pageable pageable);

    // 마이페이지에서 조회
    Page<ClubListDto> findByParticipant(Long memberId, Pageable pageable);
    Page<ClubListDto> findList(RecipeSearchCondition condition, Pageable pageable);

}
