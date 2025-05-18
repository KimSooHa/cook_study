package com.study.cook.service;

import com.study.cook.config.CacheConfig;
import com.study.cook.controller.ClubForm;
import com.study.cook.domain.*;
import com.study.cook.dto.ClubListDto;
import com.study.cook.dto.SearchCondition;
import com.study.cook.exception.FindMemberException;
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
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Import(CacheConfig.class) // Ehcache 설정 import
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

    @Autowired
    CacheManager cacheManager;

    @BeforeEach
    public void testSave() {
        Member member = new Member("testMember1", "test1", "testMember1234*", "testMember1@email.com", "010-1234-1231");
        memberRepository.save(member);
    }

    @Test
    @DisplayName("클럽 등록")
    void create() {
        // given
        Member member = getMember();
        ClubForm form = setForm();

        // 인기 레시피 리스트 캐싱
        int limit = 4;
        clubService.findLimitList(limit);
        Cache popularClubListCache = cacheManager.getCache("popularClubListCache");
        assertThat(popularClubListCache).isNotNull();

        // when
        Long clubId = clubService.create(form, member);

        // then
        assertThat(clubRepository.findById(clubId).get().getName()).isEqualTo(form.getName());
        Object cachedList = popularClubListCache.get(SimpleKeyGenerator.generateKey(new Object[]{limit}));
        assertThat(cachedList).isNull();
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
        List<ClubListDto> list = clubService.findLimitList(limit); // 인기 스터디 리스트 캐싱
        assertThat(list).isNotEmpty();

        // then
        assertThat(list.size()).isEqualTo(limit);
        Cache cache = cacheManager.getCache("popularClubListCache");

        // key가 없을 경우 SimpleKey.EMPTY 사용됨(파라미터가 있을 시 해당 파라미터를 지정해줘야 함 => SimpleKey는 내부적으로 그 파라미터 값을 포함한 객체로 만들어짐)
        // 직접 키를 지정
        Object cached = cache.get(SimpleKeyGenerator.generateKey(new Object[]{limit}));
        assertThat(cached).isNotNull();
        Object value = ((Cache.ValueWrapper) cached).get();
        assertThat(value).isInstanceOf(List.class);
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
        int limit = 4;
        clubService.findLimitList(limit); // DB에서 조회 후 캐시에 저장

        // when
        ClubForm form = setForm();
        form.setIntroduction("test");
        clubService.update(club.getId(), form);

        // then
        assertThat(club.getIntroduction()).isNotEqualTo(introduction);

        Cache popularClubListCache = cacheManager.getCache("popularClubListCache");
        Object cachedList = popularClubListCache.get(SimpleKeyGenerator.generateKey(new Object[]{limit}));

        // 수정될 때 캐시 무효화
        assertThat(cachedList).isNull();
    }

    @Test
    @DisplayName("클럽 삭제")
    void delete() {
        // given
        Club club = new Club("테스트용 쿡스터디 모집", "테스트", 5, "추후 공지");
        Member member = memberRepository.findByLoginId("test1").get();
        save(club, member);

        int limit = 4;
        clubService.findLimitList(limit); // DB에서 조회 후 캐시에 저장
        Cache popularClubListCache = cacheManager.getCache("popularClubListCache");
        assertThat(popularClubListCache).isNotNull();

        // when
        clubService.delete(club.getId());

        // then
        assertThat(clubRepository.findById(club.getId())).isEmpty();

        Object cachedList = popularClubListCache.get(SimpleKeyGenerator.generateKey(new Object[]{limit}));
        // 삭제될 때 캐시 무효화
        assertThat(cachedList).isNull();
    }

    private Member getMember() {
        return memberRepository.findByLoginId("test1").orElseThrow(() -> new FindMemberException("해당하는 회원을 찾을 수 없습니다."));
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