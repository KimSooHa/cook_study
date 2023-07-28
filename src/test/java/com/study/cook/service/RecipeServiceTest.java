package com.study.cook.service;

import com.study.cook.controller.RecipeForm;
import com.study.cook.domain.Member;
import com.study.cook.dto.RecipeListDto;
import com.study.cook.dto.SearchCondition;
import com.study.cook.exception.StoreFailException;
import com.study.cook.file.FileStore;
import com.study.cook.repository.CategoryRepository;
import com.study.cook.repository.MemberRepository;
import com.study.cook.repository.RecipeRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Slf4j
class RecipeServiceTest {

    @Autowired
    RecipeService recipeService;

    @Autowired
    RecipeRepository recipeRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    FileStore fileStore;

    @Value("${file.dir}")
    private String fileDir;

    @BeforeEach
    public void testSave() {
        Member member = new Member("testMember1", "test1", "testMember1234*", "testMember1@email.com", "010-1234-1231");
        memberRepository.save(member);
    }

    @AfterEach
    public void fileRemove() {

    }

    @Test
    @DisplayName("레시피 등록")
    void create() {
    }

    @Test
    @DisplayName("레시피 목록 조회")
    void findList() {
        // given
        SearchCondition condition = new SearchCondition();
        PageRequest pageRequest = PageRequest.of(0, 8, Sort.Direction.DESC, "regDate");

        // when
        Page<RecipeListDto> list = recipeService.findList(condition, pageRequest);

        // then
        assertThat(list.get().count()).isEqualTo(pageRequest.getPageSize());
    }

    @Test
    @DisplayName("레시피 목록 검색 조회")
    void findList_setCondition() {
        // given
        SearchCondition condition = new SearchCondition();
        condition.setTitle("피자");
        condition.setCategoryName("양식");
        PageRequest pageRequest = PageRequest.of(0, 8, Sort.Direction.DESC, "regDate");

        // when
        Page<RecipeListDto> list = recipeService.findList(condition, pageRequest);

        // then
        assertThat(list.get().count()).isEqualTo(1);
    }

    @Test
    void findByMember() {
    }

    @Test
    void findLimitList() {
    }

    @Test
    void findOneById() {
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }
}