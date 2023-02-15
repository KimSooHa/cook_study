package com.study.cook.repository;

import com.study.cook.domain.Club;
import com.study.cook.domain.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClubRepository extends JpaRepository<Club, Long>, ClubRepositoryCustom {


//    Member findByEmailAndPhoneNum(String email, String phoneNum);
//
//    Member findByLoginIdAndEmail(String loginId, String email);

}
