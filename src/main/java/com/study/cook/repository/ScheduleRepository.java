package com.study.cook.repository;

import com.study.cook.domain.Category;
import com.study.cook.domain.CookingRoom;
import com.study.cook.domain.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    Schedule findByCookingRoomIdAndStartTime(Long cookingRoomId, LocalTime startTime);

    Schedule findByStartTime(LocalTime startTime);

    List<Schedule> findByCookingRoomId(Long cookingRoomId);

    Long countByStartTime(LocalTime startTime);

    Long deleteByStartTime(LocalTime startTime);



}
