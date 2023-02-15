package com.study.cook.service;

import com.study.cook.domain.CookingRoom;
import com.study.cook.domain.Schedule;
import com.study.cook.repository.CookingRoomRepository;
import com.study.cook.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CookingRoomService {

    private final CookingRoomRepository cookingRoomRepository;
    private final ScheduleRepository scheduleRepository;

    /**
     * 요리실 등록
     */
    @Transactional  // 변경해야 하기 때문에 읽기, 쓰기가 가능해야 함
    public Long create(CookingRoom cookingRoom, List<Schedule> schedules) {

        for (Schedule schedule : schedules) {
            Schedule createdSchedule = new Schedule(schedule.getStartTime(), schedule.getEndTime());
            scheduleRepository.save(createdSchedule);
            CookingRoom.createCookingRoom(cookingRoom, createdSchedule);
        }

        cookingRoomRepository.save(cookingRoom);

        return cookingRoom.getId();
    }


    /**
     * 전체 조회
     */
    public List<CookingRoom> findList() {
        return cookingRoomRepository.findAll();
    }

    /**
     * 단건 조회
     */
    public CookingRoom findOneById(Long scheduleId) {
        return cookingRoomRepository.findById(scheduleId).orElse(null);
    }

    public CookingRoom findOneByRoomNum(int roomNum) {
        return cookingRoomRepository.findByRoomNum(roomNum).orElse(null);
    }


    @Transactional
    public void update(Long id, int maxCount, int roomNum) {
        CookingRoom cookingRoom = cookingRoomRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("no such data"));
        cookingRoom.setMaxCount(maxCount);
        cookingRoom.setRoomNum(roomNum);
    }

    @Transactional
    public void delete(CookingRoom cookingRoom) {
        cookingRoomRepository.delete(cookingRoom);
    }

}
