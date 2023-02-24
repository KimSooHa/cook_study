package com.study.cook.service;

import com.study.cook.controller.*;
import com.study.cook.domain.*;
import com.study.cook.exception.StoreFailException;
import com.study.cook.file.FileStore;
import com.study.cook.repository.CookingRoomRepository;
import com.study.cook.repository.RecipeRepository;
import com.study.cook.repository.ReservationRepository;
import com.study.cook.repository.ScheduleRepository;
import com.study.cook.util.DateParser;
import com.study.cook.util.MemberFinder;
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

    private final CookingRoomRepository cookingRoomRepository;
    private final ScheduleRepository scheduleRepository;
    private final CookingRoomService cookingRoomService;
    private final ReservationRepository reservationRepository;
    private final RecipeRepository recipeRepository;
    private final DateParser dateParser;
    private final MemberFinder memberFinder;
    private final FileStore fileStore;

    /**
     * 레시피 등록
     */
    @Transactional  // 변경해야 하기 때문에 읽기, 쓰기가 가능해야 함
    public Long create(RecipeForm form, MultipartFile file, Member member) throws StoreFailException {

        Photo photo = null;
        try {
            photo = fileStore.storeFile(file);
        } catch (IOException e) {
            throw new StoreFailException(e);
        }

        Recipe recipe = new Recipe(form.getIntroduction(), photo, form.getIngredients(), form.getCookingTime(), form.getServings());

        Recipe.createRecipe(recipe, member, form.getCategory(), photo);
        recipeRepository.save(recipe);

        return recipe.getId();
    }


    /**
     * 전체 조회
     */
    public List<Recipe> findList() {
        return recipeRepository.findAll();
    }

//    public Optional<List<Recipe>> findByCookingRoomIdAndDate(Long cookingRoomId, String date) {
//        return recipeRepository.findAll();
//    }


//    public Optional<List<Recipe>> findByMember(Member member) {
//        return recipeRepository.findByMemberId(member.getId());
//    }

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

        recipe.setCategory(form.getCategory());
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
