package com.study.cook.repository;

import com.study.cook.dto.ClubListDto;
import com.study.cook.dto.SearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ClubRepositoryCustom {

    // 마이페이지에서 조회
    Page<ClubListDto> findByParticipant(Long memberId, SearchCondition condition, Pageable pageable);
    Page<ClubListDto> findByMemberId(Long memberId, SearchCondition condition, Pageable pageable);
    Page<ClubListDto> findList(SearchCondition condition, Pageable pageable);
    List<ClubListDto> findList(int length);

}
