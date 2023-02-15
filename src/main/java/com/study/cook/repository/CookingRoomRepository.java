package com.study.cook.repository;

import com.study.cook.domain.CookingRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CookingRoomRepository extends JpaRepository<CookingRoom, Long> {

    Optional<CookingRoom> findByRoomNum(int roomNum);
}
