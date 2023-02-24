package com.study.cook.service;

import com.study.cook.controller.RecipeFieldForm;
import com.study.cook.controller.RecipeForm;
import com.study.cook.domain.Member;
import com.study.cook.domain.Photo;
import com.study.cook.domain.Recipe;
import com.study.cook.domain.RecipeField;
import com.study.cook.exception.StoreFailException;
import com.study.cook.file.FileStore;
import com.study.cook.repository.*;
import com.study.cook.util.DateParser;
import com.study.cook.util.MemberFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RecipeFieldService {

    private final CookingRoomRepository cookingRoomRepository;
    private final ScheduleRepository scheduleRepository;
    private final CookingRoomService cookingRoomService;
    private final ReservationRepository reservationRepository;
    private final RecipeRepository recipeRepository;
    private final RecipeFieldRepository recipeFieldRepository;
    private final DateParser dateParser;
    private final MemberFinder memberFinder;
    private final FileStore fileStore;

    /**
     * 레시피 등록
     */
    @Transactional  // 변경해야 하기 때문에 읽기, 쓰기가 가능해야 함
    public Long create(Long recipeId, String content, MultipartFile file, Member member) throws StoreFailException {

        Photo photo = null;
        try {
            photo = fileStore.storeFile(file);
        } catch (IOException e) {
            throw new StoreFailException(e);
        }

        RecipeField recipeField = new RecipeField(photo, content);
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow();
        RecipeField.createRecipeField(recipeField, member, recipe);
        Photo.createPhoto(photo, recipeField);

        recipeRepository.save(recipe);

        return recipe.getId();
    }

//    @Transactional  // 변경해야 하기 때문에 읽기, 쓰기가 가능해야 함
//    public Long create(Long recipeId, RecipeFieldForm form, Member member) throws StoreFailException {
//
//        Photo photo = null;
//        try {
//            photo = fileStore.storeFile(form.getImageFile());
//        } catch (IOException e) {
//            throw new StoreFailException(e);
//        }
//
//        RecipeField recipeField = new RecipeField(photo, form.getContent());
//        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow();
//        RecipeField.createRecipeField(recipeField, member, recipe);
//        if(form.getImageFile() != null)
//            Photo.createPhoto(photo, recipeField);
//
//        recipeRepository.save(recipe);
//
//        return recipe.getId();
//    }


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
    public RecipeField findOneById(Long recipeFieldId) {
        return recipeFieldRepository.findById(recipeFieldId).orElseThrow();
    }


    @Transactional
    public void update(Long id, RecipeFieldForm form) throws StoreFailException {
        RecipeField recipeField = recipeFieldRepository.findById(id).orElseThrow();
        Photo photo;
        try {
            photo = fileStore.storeFile(form.getImageFile());
        } catch (IOException e) {
            throw new StoreFailException(e);
        }
        recipeField.setPhoto(photo);
        recipeField.setContent(form.getContent());
    }

    @Transactional
    public void delete(Long id) {
        RecipeField recipeField = findOneById(id);
        recipeFieldRepository.delete(recipeField);
    }

}
