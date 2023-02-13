package com.study.cook.service;

import com.study.cook.domain.Schedule;
import com.study.cook.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;


    /**
     * 카테고리 등록
     */
    @Transactional  // 변경해야 하기 때문에 읽기, 쓰기가 가능해야 함
    public Long create(Schedule schedule) {
        scheduleRepository.save(schedule);
        return schedule.getId();
    }


    /**
     * 전체 조회
     */
    public List<Schedule> findList() {
        return scheduleRepository.findAll();
    }

    /**
     * 단건 조회
     */
    public Schedule findOneById(Long scheduleId) {
        return scheduleRepository.findById(scheduleId).orElse(null);
    }


    @Transactional
    public void update(Long id, LocalTime startTime, LocalTime endTime) {
        Schedule schedule = scheduleRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("no such data"));
        schedule.setStartTime(startTime);
        schedule.setEndTime(endTime);
    }

    @Transactional
    public void delete(Schedule schedule) {
        scheduleRepository.delete(schedule);
    }

}
