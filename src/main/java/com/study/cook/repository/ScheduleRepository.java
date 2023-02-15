package com.study.cook.repository;

import com.study.cook.domain.Category;
import com.study.cook.domain.CookingRoom;
import com.study.cook.domain.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    Schedule findByCookingRoomAndStartTime(int cookingRoomId, LocalTime startTime);

    List<Schedule> findByCookingRoom(CookingRoom cookingRoom);

    Long countByStartTime(LocalTime startTime);

    Long deleteByStartTime(LocalTime startTime);



}
