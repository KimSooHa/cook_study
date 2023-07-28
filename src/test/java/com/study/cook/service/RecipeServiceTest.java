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
import java.util.List;

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
        // given
        Member member = memberRepository.findByLoginId("test1").get();

        RecipeForm form = new RecipeForm();
        form.setTitle("피자 만들기");
        form.setIntroduction("피자 만드는 레시피를 공유해요~");
        form.setIngredients("토마토 소스, 양파, 등 ..");
        form.setCategoryId(categoryRepository.findAll().get(1).getId());
        form.setServings(2);
        form.setCookingTime(2);
        MockMultipartFile file = null;
        try {
            file = new MockMultipartFile("file",
                    "pizza_img.jpeg",
                    "application/json",
                    new FileInputStream(fileDir));
        } catch (IOException e) {
            throw new StoreFailException(e);
        }

        // when
        Long recipeId = recipeService.create(form, file, member);

        // then
        assertThat(recipeRepository.findById(recipeId).get().getPhoto().getUploadFileName()).isEqualTo(file.getOriginalFilename());
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
    @DisplayName("댓글 많은 순으로 제한된 레시피 목록 조회")
    void findLimitList() {
        // given
        int limit = 4;

        // when
        List<RecipeListDto> limitList = recipeService.findLimitList(limit);

        // then
        assertThat(limitList.size()).isEqualTo(limit);
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