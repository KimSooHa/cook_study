package com.study.cook.service;

import com.study.cook.SessionConst;
import com.study.cook.controller.ClubForm;
import com.study.cook.domain.*;
import com.study.cook.dto.ClubListDto;
import com.study.cook.dto.SearchCondition;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Slf4j
class ClubServiceTest {

    @Autowired
    ClubService clubService;

    @Autowired
    ClubRepository clubRepository;

    @Autowired
    ParticipationRepository participationRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    MemberRepository memberRepository;

    private HttpSession session;

    @BeforeEach
    public void testSave() {
        Member member = new Member("testMember1", "test1", "testMember1234*", "testMember1@email.com", "010-1234-1231");
        memberRepository.save(member);
    }

    @Test
    @DisplayName("클럽 등록")
    void create() {
        // given
        ClubForm form = setForm();
        HttpSession session = setSession();

        // when
        Long clubId = clubService.create(form, this.session);

        // then
        assertThat(clubRepository.findById(clubId).get().getName()).isEqualTo(form.getName());
    }

    @Test
    @DisplayName("클럽 목록 조회")
    void findList() {
        // given
        SearchCondition condition = new SearchCondition();
        PageRequest pageRequest = PageRequest.of(0, 8, Sort.Direction.DESC, "regDate");

        // when
        Page<ClubListDto> list = clubService.findList(condition, pageRequest);

        // then
        assertThat(list.get().count()).isEqualTo(pageRequest.getPageSize());
    }

    @Test
    @DisplayName("참여자 많은 순으로 갯수 제한된 클럽 목록 조회")
    void findLimitList() {
        // given
        int limit = 4;

        // when
        List<ClubListDto> list = clubService.findLimitList(limit);

        // then
        assertThat(list.size()).isEqualTo(limit);
    }

    @Test
    @DisplayName("아이디로 클럽 조회")
    void findOneById() {
        // given
        Club club = new Club("테스트용 쿡스터디 모집", "테스트", 5, "추후 공지");

        Member member = memberRepository.findByLoginId("test1").get();
        save(club, member);

        // when
        Club findClub = clubService.findOneById(club.getId());

        // then
        assertThat(findClub.getName()).isEqualTo(club.getName());
    }

    @Test
    @DisplayName("참여하는 클럽 목록 조회")
    void findByParticipant() {
        // given
        SearchCondition condition = new SearchCondition();
        PageRequest pageRequest = PageRequest.of(0, 8, Sort.Direction.DESC, "regDate");

        Club club = new Club("테스트용 쿡스터디 모집", "테스트", 5, "추후 공지");
        Member member = memberRepository.findByLoginId("test1").get();
        save(club, member);
        participate(club, member);

        // when
        Page<ClubListDto> list = clubService.findByParticipant(member.getId(), condition, pageRequest);

        // then
        assertThat(list.get().count()).isEqualTo(1);
    }

    @Test
    @DisplayName("회원이 운영하는 클럽목록 조회")
    void findByMember() {
        // given
        SearchCondition condition = new SearchCondition();
        PageRequest pageRequest = PageRequest.of(0, 8, Sort.Direction.DESC, "regDate");
        Club club = new Club("테스트용 쿡스터디 모집", "테스트", 5, "추후 공지");
        Member member = memberRepository.findByLoginId("test1").get();
        save(club, member);

        // when
        Page<ClubListDto> list = clubService.findByMember(member.getId(), condition, pageRequest);

        // then
        assertThat(list.get().count()).isEqualTo(1);
    }

    @Test
    @DisplayName("클럽 수정")
    void update() {
        // given
        Club club = new Club("테스트용 쿡스터디 모집", "테스트", 5, "추후 공지");
        Member member = memberRepository.findByLoginId("test1").get();
        save(club, member);
        String introduction = club.getIntroduction();

        // when
        ClubForm form = setForm();
        form.setIntroduction("test");
        clubService.update(club.getId(), form);

        // then
        assertThat(club.getIntroduction()).isNotEqualTo(introduction);
    }

    @Test
    @DisplayName("클럽 삭제")
    void delete() {
        // given
        Club club = new Club("테스트용 쿡스터디 모집", "테스트", 5, "추후 공지");
        Member member = memberRepository.findByLoginId("test1").get();
        save(club, member);

        // when
        clubService.delete(club.getId());

        // then
        assertThat(clubRepository.findById(club.getId())).isEmpty();
    }

    private HttpSession setSession() {
        session = new MockHttpSession();
        Member member = memberRepository.findByLoginId("test1").get();
        session.setAttribute(SessionConst.LOGIN_MEMBER, member);
        return session;
    }

    private ClubForm setForm() {
        ClubForm form = new ClubForm();
        form.setName("테스트용 쿡스터디 모집");
        form.setIntroduction("테스트");
        form.setMaxCount(5);
        form.setIngredients("추후 공지");
        form.setCategoryId(categoryRepository.findAll().get(0).getId());
        form.setReservationIds(new ArrayList<>());
        return form;
    }

    private void participate(Club club, Member member) {
        Participation participation = Participation.createParticipation(member, club);
        participationRepository.save(participation);
    }

    private void save(Club club, Member member) {
        List<Reservation> reservations = new ArrayList<>();
        Category category = categoryRepository.findAll().get(0);
        Club createdClub = Club.createClub(club, member, category, reservations);
        clubRepository.save(createdClub);
    }
}