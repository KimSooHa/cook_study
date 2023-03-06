package com.study.cook.repository;

import com.study.cook.domain.Club;
import com.study.cook.dto.ClubListDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClubRepository extends JpaRepository<Club, Long>, ClubRepositoryCustom {


}
