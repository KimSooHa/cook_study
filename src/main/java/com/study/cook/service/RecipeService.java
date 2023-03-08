package com.study.cook.service;

import com.study.cook.controller.RecipeForm;
import com.study.cook.domain.Category;
import com.study.cook.domain.Member;
import com.study.cook.domain.Photo;
import com.study.cook.domain.Recipe;
import com.study.cook.exception.StoreFailException;
import com.study.cook.file.FileStore;
import com.study.cook.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RecipeService {
    private final CategoryService categoryService;
    private final RecipeRepository recipeRepository;
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
            throw new StoreFailException(e);
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
    public List<Recipe> findList() {
        return recipeRepository.findAll();
    }


    /**
     * 단건 조회
     */
    public Recipe findOneById(Long recipeId) {
        return recipeRepository.findById(recipeId).orElse(null);
    }


    @Transactional
    public void update(Long id, RecipeForm form) throws StoreFailException {

        Photo photo = null;
        try {
            photo = fileStore.storeFile(form.getImageFile());
        } catch (IOException e) {
            throw new StoreFailException(e);
        }

        Recipe recipe = recipeRepository.findById(id).orElseThrow();
        Category category = categoryService.findOneById(form.getCategoryId());
        recipe.setCategory(category);
        recipe.setPhoto(photo);
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
