package com.study.cook.service;

import com.study.cook.controller.RecipeForm;
import com.study.cook.domain.*;
import com.study.cook.dto.*;
import com.study.cook.exception.FindRecipeException;
import com.study.cook.exception.StoreFailException;
import com.study.cook.file.FileStore;
import com.study.cook.repository.RecipeFieldRepository;
import com.study.cook.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class RecipeService {
    private final CategoryService categoryService;
    private final RecipeRepository recipeRepository;
    private final RecipeFieldRepository recipeFieldRepository;
    private final FileStore fileStore;

    /**
     * 레시피 등록
     */
    @Transactional
    @CacheEvict(value = "popularRecipeListCache", allEntries = true)  // 인기 레시피 리스트 캐시 제거
    public Long create(RecipeForm form, MultipartFile file, Member member) {

        Photo photo = null;
        try {
            photo = fileStore.storeFile(file);
        } catch (IOException e) {
            throw new StoreFailException("등록 실패: 이미지 파일 저장 실패", e);
        }

        Category category = categoryService.findOneById(form.getCategoryId());
        Recipe recipe = new Recipe(form.getTitle(), form.getIntroduction(), photo, form.getIngredients(), form.getCookingTime(), form.getServings());
        Recipe createdRecipe = Recipe.createRecipe(recipe, member, category, photo);
        recipeRepository.save(createdRecipe);

        return recipe.getId();
    }


    /**
     * 전체 조회
     */
    public Page<RecipeListDto> findList(SearchCondition condition, Pageable pageable) {
        return recipeRepository.findList(condition, pageable);
    }

    /**
     * 등록한 레시피 리스트 조회
     */
    public Page<RecipeListDto> findByMember(Long memberId, SearchCondition condition, Pageable pageable) {
        return recipeRepository.findByMemberId(memberId, condition, pageable);
    }

    /**
     * 정해진 갯수의 그룹 리스트 조회
     */
    @Cacheable(value = "popularRecipeListCache")
    public List<RecipeListDto> findLimitList(int length) {
        return recipeRepository.findList(length);
    }


    /**
     * 단건 조회
     */
    public Recipe findOneById(Long recipeId) {
        return recipeRepository.findById(recipeId).orElse(null);
    }
    @Cacheable(value = "recipeCache", key = "#recipeId")
    public RecipeDetailDto findDetailOneById(Long recipeId) {
        log.info("📦 DB에서 레시피 조회: " + recipeId); // 로그 찍기
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(() -> new FindRecipeException("해당하는 레시피가 없습니다."));
        Optional<List<RecipeField>> recipeFields = recipeFieldRepository.findByRecipeId(recipeId);
        List<RecipeFieldDto> recipeFieldDtoList = new ArrayList<>();
        if (recipeFields.isPresent()) {
            recipeFieldDtoList = recipeFields.get().stream().map(rf -> {
                RecipeFieldDto recipeFieldDto = new RecipeFieldDto(rf.getPhoto(), rf.getContent());
                return recipeFieldDto;
            }).collect(toList());
        }

        RecipeDetailDto recipeDetailDto = new RecipeDetailDto(new RecipeDto(recipe.getTitle(), recipe.getIntroduction(), recipe.getPhoto(), recipe.getIngredients(), recipe.getCookingTime(), recipe.getServings(), recipe.getCategory().getName()),
                recipeFieldDtoList, recipe.getMember());
        return recipeDetailDto;
    }


    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "recipeCache", key = "#recipeId"),  // 상세 조회 캐시 제거
        @CacheEvict(value = "popularRecipeListCache", allEntries = true)  // 인기 레시피 리스트 캐시 제거
    })
    public void update(Long recipeId, RecipeForm form, Optional<MultipartFile> file) {

        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow();

        Photo photo = null;
        try {
            // 파일이 존재하며 기존 이미지와 같지 않으면
            if (file.isPresent()) {
                photo = fileStore.storeFile(file.get());
            }
        } catch (IOException e) {
            throw new StoreFailException("등록 실패: 이미지 파일 저장 실패", e);
        }

        if (photo != null) {
            Photo originPhoto = recipe.getPhoto();
            // 기존 파일 삭제
            fileStore.deleteFile(originPhoto.getStoreFileName());
            recipe.getPhoto().setUploadFileName(photo.getUploadFileName());
            recipe.getPhoto().setStoreFileName(photo.getStoreFileName());
        }

        Category category = categoryService.findOneById(form.getCategoryId());
        recipe.setCategory(category);
        recipe.setServings(form.getServings());
        recipe.setIngredients(form.getIngredients());
        recipe.setIntroduction(form.getIntroduction());
        recipe.setCookingTime(form.getCookingTime());
    }

    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "recipeCache", key = "#recipeId"),  // 상세 조회 캐시 제거
        @CacheEvict(value = "popularRecipeListCache", allEntries = true)  // 인기 레시피 리스트 캐시 제거
    })
    public void delete(Long recipeId) {
        Recipe recipe = findOneById(recipeId);

        Photo photo = recipe.getPhoto();
        fileStore.deleteFile(photo.getStoreFileName());

        List<RecipeField> recipeFields = recipe.getRecipeFields();
        recipeFields.stream().forEach(rf -> {
            Photo rfPhoto = rf.getPhoto();
            fileStore.deleteFile(rfPhoto.getStoreFileName());
        });
        recipeRepository.delete(recipe);
    }

}
