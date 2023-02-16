package com.study.cook.repository;

import com.study.cook.domain.Participation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {

    Long countByClub(Long clubId);

    Optional<List<Participation>> findByMember(Long memberId);

    Optional<Participation> findByClubAndMember(Long clubId, Long memberId);

}
