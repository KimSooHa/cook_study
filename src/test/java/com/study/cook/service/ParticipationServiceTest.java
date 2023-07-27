package com.study.cook.service;

import com.study.cook.domain.*;
import com.study.cook.exception.ParticipateFailException;
import com.study.cook.repository.CategoryRepository;
import com.study.cook.repository.ClubRepository;
import com.study.cook.repository.MemberRepository;
import com.study.cook.repository.ParticipationRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.study.cook.domain.ClubStatus.COMP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
    @DisplayName("참여자 등록 성공")
    void tryToCreate_success() {
        // given
        Club club = new Club("테스트용 쿡스터디 모집", "테스트", 5, "추후 공지");
        Member member = memberRepository.findByLoginId("test1").get();
        clubSave(club, member);

        // when
        Long participationId = participationService.tryToCreate(club.getId(), member);

        // then
        assertThat(clubRepository.findById(club.getId()).get().getParticipations().get(0).getId()).isEqualTo(participationId);
    }

    @Test
    @DisplayName("참여자 등록 실패")
    void tryToCreate_fail() {
        // given
        Club club = new Club("테스트용 쿡스터디 모집", "테스트", 2, "추후 공지");
        List<Member> members = memberRepository.findAll();
        clubSave(club, members.get(0));

        // when
        for (int i = 0; i < 2; i++)
            participate(club, members.get(i));
        club.setStatus(COMP);

        // then
        assertThatThrownBy(() -> participationService.tryToCreate(club.getId(), members.get(2))).isInstanceOf(ParticipateFailException.class);
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
    @DisplayName("회원과 클럽으로 참여 조회")
    void findByClubAndMember() {
        // given
        Club club = new Club("테스트용 쿡스터디 모집", "테스트", 5, "추후 공지");
        Member member = memberRepository.findByLoginId("test1").get();
        clubSave(club, member);
        participate(club, member);

        // when
        Participation participation = participationService.findByClubAndMember(club, member);

        // then
        assertThat(participation.getMember().getId()).isEqualTo(member.getId());
        assertThat(participation.getClub().getId()).isEqualTo(club.getId());
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