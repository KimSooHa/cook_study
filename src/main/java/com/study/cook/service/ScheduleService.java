package com.study.cook.service;

import com.study.cook.domain.CookingRoom;
import com.study.cook.domain.Schedule;
import com.study.cook.repository.CookingRoomRepository;
import com.study.cook.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final CookingRoomRepository cookingRoomRepository;


    /**
     * 스케줄 등록
     */
    @Transactional  // 변경해야 하기 때문에 읽기, 쓰기가 가능해야 함
    public Long create(Schedule schedule, CookingRoom cookingRoom) {
        // 생성하는 시간 요리실과 매칭
        schedule.setCookingRoom(cookingRoom);
        cookingRoom.getSchedules().add(schedule);

        scheduleRepository.save(schedule);
        return schedule.getId();
    }

    @Transactional  // 변경해야 하기 때문에 읽기, 쓰기가 가능해야 함
    public Long createAndMatchAll(LocalTime startTime, LocalTime endTime) {
        // 생성하는 시간 모든 요리실과 매칭해서 추가
        List<CookingRoom> cookingRooms = cookingRoomRepository.findAll();

        for (CookingRoom cookingRoom : cookingRooms) {
            Schedule schedule = new Schedule(startTime, endTime);
            schedule.setCookingRoom(cookingRoom);
            cookingRoom.getSchedules().add(schedule);
            scheduleRepository.save(schedule);
        }
        return scheduleRepository.countByStartTime(startTime);
    }


    /**
     * 전체 조회
     */
    public List<Schedule> findList() {
        return scheduleRepository.findAll();
    }

    public List<Schedule> findListByCookingRoom(CookingRoom cookingRoom) {
        return scheduleRepository.findByCookingRoomId(cookingRoom.getId());
    }

    public Schedule findOneByCookingRoomAndStartTime(CookingRoom cookingRoom, LocalTime time) {
        return scheduleRepository.findByCookingRoomIdAndStartTime(cookingRoom.getId(), time);
    }

    /**
     * 단건 조회
     */
    public Schedule findOneById(Long scheduleId) {
        return scheduleRepository.findById(scheduleId).orElseThrow(() -> new IllegalArgumentException("no such data"));
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

    @Transactional
    public void deleteAllByTime(LocalTime startTime) {
        scheduleRepository.deleteByStartTime(startTime);
    }

}
