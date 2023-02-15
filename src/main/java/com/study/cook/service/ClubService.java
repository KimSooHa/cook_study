package com.study.cook.service;

import com.study.cook.domain.Category;
import com.study.cook.domain.Club;
import com.study.cook.domain.Reservation;
import com.study.cook.repository.ClubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ClubService {

    private final ClubRepository clubRepository;


    /**
     * 클럽 등록
     */
    @Transactional  // 변경해야 하기 때문에 읽기, 쓰기가 가능해야 함
    public Long create(Club club) {
        clubRepository.save(club);
        return club.getId();
    }


    /**
     *그룹 전체 조회
     */
    public List<Club> findList() {
        return clubRepository.findAll();
    }

    /**
     * 그룹 조회
     */
    public Club findOneById(Long clubId) {
        return clubRepository.findById(clubId).orElse(null);
    }

    /**
     * 그룹 리스트 조회
     */
//    public Member findOne(MemberLoginIdSearchCondition condition) {
//        return clubRepository.findByEmailAndPhoneNum(condition.getEmail(), condition.getPhoneNum());
//    }
//
//    public Member findOne(MemberPwdSearchCondition condition) {
//        return clubRepository.findByLoginIdAndEmail(condition.getLoginId(), condition.getEmail());
//    }

    @Transactional
    public void update(Long id, String name, String introduction, int maxCount, String ingredients, Category category, Reservation reservation) {
        Club club = clubRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("no such data"));
        club.setName(name);
        club.setIntroduction(introduction);
        club.setMaxCount(maxCount);
        club.setCategory(category);
        club.setIngredients(ingredients);
        club.setReservation(reservation);
    }

    @Transactional
    public void delete(Club club) {
        clubRepository.delete(club);
    }
}
