package com.study.cook.service;

import com.study.cook.controller.RecipeForm;
import com.study.cook.domain.Category;
import com.study.cook.domain.Member;
import com.study.cook.domain.Photo;
import com.study.cook.domain.Recipe;
import com.study.cook.dto.ClubListDto;
import com.study.cook.dto.RecipeListDto;
import com.study.cook.dto.SearchCondition;
import com.study.cook.exception.StoreFailException;
import com.study.cook.file.FileStore;
import com.study.cook.repository.PhotoRepository;
import com.study.cook.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RecipeService {
    private final CategoryService categoryService;
    private final RecipeRepository recipeRepository;
    private final PhotoRepository photoRepository;
    private final FileStore fileStore;

    /**
     * 레시피 등록
     */
    @Transactional
    public Long create(RecipeForm form, MultipartFile file, Member member) throws StoreFailException {

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
    public List<RecipeListDto> findLimitList(int length) {
        return recipeRepository.findList(length);
    }


    /**
     * 단건 조회
     */
    public Recipe findOneById(Long recipeId) {
        return recipeRepository.findById(recipeId).orElse(null);
    }


    @Transactional
    public void update(Long id, RecipeForm form, Optional<MultipartFile> file) throws StoreFailException {

        Recipe recipe = recipeRepository.findById(id).orElseThrow();

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
    public void delete(Long recipeId) {
        Recipe recipe = findOneById(recipeId);
        recipeRepository.delete(recipe);
    }

}
