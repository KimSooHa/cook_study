package com.study.cook.repository;

import com.study.cook.dto.ClubListDto;
import com.study.cook.dto.SearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ClubRepositoryCustom {

//    Page<ClubListDto> findAll(RecipeSearchCondition condition, Pageable pageable);

    // 마이페이지에서 조회
    Page<ClubListDto> findByParticipant(Long memberId, Pageable pageable);
    Page<ClubListDto> findList(SearchCondition condition, Pageable pageable);
    List<ClubListDto> findList(int length);

}
