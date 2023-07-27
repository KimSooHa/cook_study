package com.study.cook.service;

import com.study.cook.domain.*;
import com.study.cook.repository.CategoryRepository;
import com.study.cook.repository.ClubRepository;
import com.study.cook.repository.MemberRepository;
import com.study.cook.repository.ParticipationRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Slf4j
class ParticipationServiceTest {

    @Autowired
    ParticipationService participationService;

    @Autowired
    ParticipationRepository participationRepository;

    @Autowired
    ClubRepository clubRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    MemberRepository memberRepository;

    @BeforeEach
    public void testSave() {
        Member member = new Member("testMember1", "test1", "testMember1234*", "testMember1@email.com", "010-1234-1231");
        memberRepository.save(member);
    }

    @Test
    void tryToCreate() {
    }

    @Test
    @DisplayName("클럽의 참여자 수 조회")
    void countByClub() {
        // given
        Club club = new Club("테스트용 쿡스터디 모집", "테스트", 5, "추후 공지");
        Member member = memberRepository.findByLoginId("test1").get();
        clubSave(club, member);
        participate(club, member);

        // when
        Long count = participationService.countByClub(club);

        // then
        assertThat(count).isEqualTo(1);
     }

    @Test
    void findByMember() {
    }

    @Test
    void findByClubAndMember() {
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }

    private void clubSave(Club club, Member member) {
        List<Reservation> reservations = new ArrayList<>();
        Category category = categoryRepository.findAll().get(0);
        Club createdClub = Club.createClub(club, member, category, reservations);
        clubRepository.save(createdClub);
    }

    private void participate(Club club, Member member) {
        Participation participation = Participation.createParticipation(member, club);
        participationRepository.save(participation);
    }
}