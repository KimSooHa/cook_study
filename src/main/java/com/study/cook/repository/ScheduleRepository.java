package com.study.cook.repository;

import com.study.cook.domain.Category;
import com.study.cook.domain.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {


}
