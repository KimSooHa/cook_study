package com.study.cook.repository;

import com.study.cook.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    Optional<Member> findByLoginId(String LoginId);
    Optional<Member> findByEmail(String email);

    Member findByEmailAndPhoneNum(String email, String phoneNum);

    Member findByLoginIdAndEmail(String loginId, String email);

    Long countByEmail(String email);
    Long countByLoginId(String loginId);


}
