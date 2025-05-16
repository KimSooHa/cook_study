package com.study.cook.service;

import com.study.cook.config.CacheConfig;
import com.study.cook.controller.RecipeForm;
import com.study.cook.domain.Category;
import com.study.cook.domain.Member;
import com.study.cook.domain.Photo;
import com.study.cook.domain.Recipe;
import com.study.cook.dto.RecipeDetailDto;
import com.study.cook.dto.RecipeListDto;
import com.study.cook.dto.SearchCondition;
import com.study.cook.exception.StoreFailException;
import com.study.cook.file.FileStore;
import com.study.cook.repository.CategoryRepository;
import com.study.cook.repository.MemberRepository;
import com.study.cook.repository.RecipeRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Import(CacheConfig.class) // Ehcache 설정 import
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
    CacheManager cacheManager;

    @Autowired
    FileStore fileStore;

    static String fileName = "pizza_img.jpeg";
    static String filePath = "/Users/sooha/Desktop/image/food/pizza/";

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

        RecipeForm form = setForm();

        MockMultipartFile file = null;
        try {
            file = getMockMultipartFile(fileName, filePath);
        } catch (IOException e) {
            throw new StoreFailException(e);
        }

        // 인기 레시피 리스트 캐싱
        int limit = 4;
        recipeService.findLimitList(limit);
        Cache popularRecipeListCache = cacheManager.getCache("popularRecipeListCache");
        assertThat(popularRecipeListCache).isNotNull();

        // when
        Long recipeId = recipeService.create(form, file, member);

        // then
        assertThat(recipeRepository.findById(recipeId).get().getPhoto().getUploadFileName()).isEqualTo(file.getOriginalFilename());
        Object cachedList = popularRecipeListCache.get(SimpleKeyGenerator.generateKey(new Object[]{limit}));
        assertThat(cachedList).isNull();

        // after test
        deleteFile(recipeId);
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
        assertThat(list.get().count()).isLessThan(pageRequest.getPageSize());
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
    @DisplayName("회원으로 목록 조회")
    void findByMember() {
        // given
        Member member = memberRepository.findByLoginId("test1").get();

        RecipeForm form = setForm();

        Long recipeId = save(member, form, fileName, filePath);

        SearchCondition condition = new SearchCondition();
        PageRequest pageRequest = PageRequest.of(0, 8, Sort.Direction.DESC, "regDate");

        // when
        Page<RecipeListDto> list = recipeService.findByMember(member.getId(), condition, pageRequest);

        // then
        assertThat(list.get().count()).isEqualTo(1);

        // after test
        deleteFile(recipeId);
    }

    @Test
    @DisplayName("댓글 많은 순으로 제한된 레시피 목록 조회")
    void findLimitList() {
        // given
        int limit = 4;

        // when
        List<RecipeListDto> limitList = recipeService.findLimitList(limit);
        assertThat(limitList).isNotEmpty();
        recipeService.findLimitList(limit);

        // then
        assertThat(limitList.size()).isEqualTo(limit);
        Cache cache = cacheManager.getCache("popularRecipeListCache");

        // key가 없을 경우 SimpleKey.EMPTY 사용됨(파라미터가 있을 시 해당 파라미터를 지정해줘야 함 => SimpleKey는 내부적으로 그 파라미터 값을 포함한 객체로 만들어짐)
        // 직접 키를 지정
        Object cached = cache.get(SimpleKeyGenerator.generateKey(new Object[]{limit}));
        assertThat(cached).isNotNull();
        Object value = ((Cache.ValueWrapper) cached).get();
        assertThat(value).isInstanceOf(List.class);
    }

    @Test
    @DisplayName("아이디로 레시피 조회")
    void findOneById() {
        // given
        Member member = memberRepository.findByLoginId("test1").get();

        RecipeForm form = setForm();

        Long recipeId = save(member, form, fileName, filePath);

        // when
        Recipe recipe = recipeService.findOneById(recipeId);

        // then
        assertThat(recipe.getTitle()).isEqualTo(form.getTitle());

        // after test
        deleteFile(recipeId);
    }

    @Test
    @DisplayName("아이디로 캐시된 레시피 조회")
    void findDetailOneById() {
        // given
        Member member = memberRepository.findByLoginId("test1").get();

        RecipeForm form = setForm();

        Long recipeId = save(member, form, fileName, filePath);

        // when
        RecipeDetailDto recipeDetailDto = recipeService.findDetailOneById(recipeId);

        // then
        Cache recipeCache = cacheManager.getCache("recipeCache");
        Object cached = recipeCache.get(recipeId);
        assertThat(cached).isNotNull();
        assertThat(recipeDetailDto.getRecipe().getTitle()).isEqualTo(form.getTitle());

        // after test
        deleteFile(recipeId);
    }

    @Test
    @DisplayName("레시피 수정 - 캐시 무효화")
    void update() {
        // given
        int limit = 4;
        Member member = memberRepository.findByLoginId("test1").get();

        RecipeForm form = setForm();

        Long recipeId = save(member, form, fileName, filePath);
        RecipeDetailDto recipeDetailDto = recipeService.findDetailOneById(recipeId); // DB에서 조회 후 캐시에 저장
        recipeService.findLimitList(limit); // DB에서 조회 후 캐시에 저장

        // when
        MockMultipartFile file;
        try {
            file = getMockMultipartFile("pizza1.jpeg", filePath);
        } catch (IOException e) {
            throw new StoreFailException(e);
        }
        recipeService.update(recipeId, form, Optional.of(file));

        // then
        assertThat(recipeDetailDto.getRecipe().getImg().getUploadFileName()).isNotEqualTo(fileName);
        Cache recipeCache = cacheManager.getCache("recipeCache");
        Cache popularRecipeListCache = cacheManager.getCache("popularRecipeListCache");
        Object cachedOne = recipeCache.get(recipeId);
        Object cachedList = popularRecipeListCache.get(SimpleKeyGenerator.generateKey(new Object[]{limit}));

        // 수정될 때 캐시 무효화
        assertThat(cachedOne).isNull();
        assertThat(cachedList).isNull();

        // after test
        deleteFile(recipeId);
    }

    @Test
    @DisplayName("레시피 삭제")
    void delete() {
        // given
        Member member = memberRepository.findByLoginId("test1").get();

        RecipeForm form = setForm();

        Long recipeId = save(member, form, fileName, filePath);

        int limit = 4;
        recipeService.findDetailOneById(recipeId);
        recipeService.findLimitList(limit); // DB에서 조회 후 캐시에 저장
        Cache recipeCache = cacheManager.getCache("recipeCache");
        Cache popularRecipeListCache = cacheManager.getCache("popularRecipeListCache");
        assertThat(recipeCache).isNotNull();
        assertThat(popularRecipeListCache).isNotNull();

        // when
        recipeService.delete(recipeId);

        // then
        assertThat(recipeRepository.findById(recipeId)).isEmpty();

        Object cachedOne = recipeCache.get(recipeId);
        Object cachedList = popularRecipeListCache.get(SimpleKeyGenerator.generateKey(new Object[]{limit}));

        // 삭제될 때 캐시 무효화
        assertThat(cachedOne).isNull();
        assertThat(cachedList).isNull();
    }

    private RecipeForm setForm() {
        RecipeForm form = new RecipeForm();
        form.setTitle("피자 만들기");
        form.setIntroduction("피자 만드는 레시피를 공유해요~");
        form.setIngredients("토마토 소스, 양파, 등 ..");
        form.setCategoryId(categoryRepository.findAll().get(1).getId());
        form.setServings(2);
        form.setCookingTime(2);
        return form;
    }

    private static MockMultipartFile getMockMultipartFile(String fileName, String filePath) throws IOException {
        return new MockMultipartFile(
                "image",
                    fileName,
                    "image/png",
                    new FileInputStream(filePath + fileName));
    }

    private Long save(Member member, RecipeForm form, String fileName, String filePath) {
        MockMultipartFile file;
        Photo photo;
        try {
            file = getMockMultipartFile(fileName, filePath);
            photo = fileStore.storeFile(file);
        } catch (IOException e) {
            throw new StoreFailException("등록 실패: 이미지 파일 저장 실패", e);
        }

        Category category = categoryRepository.findById(form.getCategoryId()).get();
        Recipe recipe = new Recipe(form.getTitle(), form.getIntroduction(), photo, form.getIngredients(), form.getCookingTime(), form.getServings());
        Recipe createdRecipe = Recipe.createRecipe(recipe, member, category, photo);
        recipeRepository.save(createdRecipe);

        return recipe.getId();
    }

    private void deleteFile(Long recipeId) {
        String storeFileName = recipeRepository.findById(recipeId).get().getPhoto().getStoreFileName();
        fileStore.deleteFile(storeFileName);
    }
}