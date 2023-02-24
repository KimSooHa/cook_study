package com.study.cook.repository;

import com.study.cook.domain.Participation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {

    Long countByClubId(Long clubId);

    Optional<List<Participation>> findByMember(Long memberId);

    Optional<Participation> findByClubIdAndMemberId(Long clubId, Long memberId);

}
