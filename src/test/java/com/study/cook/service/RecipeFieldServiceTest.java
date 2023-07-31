package com.study.cook.service;

import com.study.cook.controller.RecipeForm;
import com.study.cook.domain.*;
import com.study.cook.exception.StoreFailException;
import com.study.cook.file.FileStore;
import com.study.cook.repository.CategoryRepository;
import com.study.cook.repository.MemberRepository;
import com.study.cook.repository.RecipeFieldRepository;
import com.study.cook.repository.RecipeRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Slf4j
class RecipeFieldServiceTest {

    @Autowired
    RecipeFieldService recipeFieldService;

    @Autowired
    RecipeFieldRepository recipeFieldRepository;

    @Autowired
    RecipeRepository recipeRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    FileStore fileStore;

    private String filePath = "/Users/sooha/Desktop/image/food/";
    private String fileName = "pizza_img.jpeg";
    private Long recipeId;

    @BeforeEach
    public void testSave() {
        Member member = new Member("testMember1", "test1", "testMember1234*", "testMember1@email.com", "010-1234-1231");
        memberRepository.save(member);

        RecipeForm form = setForm();
        recipeId = save(member, form, fileName, filePath);
    }

    @AfterEach
    public void testDelete() {
        deleteFile(recipeId);
    }

    @Test
    @DisplayName("레시피 필드 생성")
    void create() {
        // given
        Member member = memberRepository.findByLoginId("test1").get();

        MockMultipartFile fieldFile;
        try {
            fieldFile = getMockMultipartFile(fileName, filePath);
        } catch (IOException e) {
            throw new StoreFailException(e);
        }

        // when
        recipeFieldService.create(recipeId, "test", fieldFile, member);

        // then
        assertThat(recipeFieldRepository.findByRecipeId(recipeId).get().size()).isEqualTo(1);
        assertThat(recipeRepository.findById(recipeId).get().getRecipeFields().get(0).getPhoto().getUploadFileName()).isEqualTo(fileName);
    }

    @Test
    @DisplayName("레시피로 조회")
    void findByRecipeId() {
        // given
        Member member = memberRepository.findByLoginId("test1").get();
        fieldSave(member);

        // when
        List<RecipeField> recipeFields = recipeFieldService.findByRecipeId(recipeId).get();

        // then
        assertThat(recipeFields.size()).isEqualTo(1);
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

    private void fieldSave(Member member) {
        MockMultipartFile file;
        Photo photo;
        try {
            file = getMockMultipartFile(fileName, filePath);
            photo = fileStore.storeFile(file);
        } catch (IOException e) {
            throw new StoreFailException(e);
        }

        RecipeField recipeField = new RecipeField(photo, "test");
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow();
        RecipeField.createRecipeField(recipeField, member, recipe);
        Photo.createPhoto(photo, recipeField);

        recipeRepository.save(recipe);
    }

    private void deleteFile(Long recipeId) {
        String storeFileName = recipeRepository.findById(recipeId).get().getPhoto().getStoreFileName();
        fileStore.deleteFile(storeFileName);

        List<RecipeField> recipeFields = recipeRepository.findById(recipeId).get().getRecipeFields();
        if(!recipeFields.isEmpty()) {
            String storeFileFieldName = recipeFields.get(0).getPhoto().getStoreFileName();
            fileStore.deleteFile(storeFileFieldName);
        }
    }
}