package com.study.cook.service;

import com.study.cook.controller.RecipeFieldForm;
import com.study.cook.domain.Member;
import com.study.cook.domain.Photo;
import com.study.cook.domain.Recipe;
import com.study.cook.domain.RecipeField;
import com.study.cook.exception.StoreFailException;
import com.study.cook.file.FileStore;
import com.study.cook.repository.RecipeFieldRepository;
import com.study.cook.repository.RecipeRepository;
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
    private final RecipeRepository recipeRepository;
    private final RecipeFieldRepository recipeFieldRepository;
    private final FileStore fileStore;

    /**
     * 레시피 필드 등록
     */
    @Transactional
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


    /**
     * 전체 조회
     */
    public List<Recipe> findList() {
        return recipeRepository.findAll();
    }

    /**
     * 레시피로 조회
     */
    public Optional<List<RecipeField>> findByRecipeId(Long recipeId) {
        return recipeFieldRepository.findByRecipeId(recipeId);
    }

    /**
     * 단건 조회
     */
    public RecipeField findOneById(Long recipeFieldId) {
        return recipeFieldRepository.findById(recipeFieldId).orElseThrow();
    }


    @Transactional
    public void update(Long id, List<String> fieldForms, Optional<List<MultipartFile>> files, Optional<List<Integer>> imgIndexes, Member member) throws StoreFailException {
        List<RecipeField> recipeFields = findByRecipeId(id).get();
        int cnt = 0;
        for (int i = 0; i < recipeFields.size(); i++) {
            // 수정에서 없앤 폼 삭제하기
            if (i >= fieldForms.size()) {
                delete(recipeFields.get(i).getId());
                continue;
            }

            RecipeField recipeField = recipeFields.get(i);
            recipeField.setContent(fieldForms.get(i));
            if (imgIndexes.isPresent() && imgIndexes.get().contains(i)) {
                Photo photo = null;
                try {
                    // 기존 파일 삭제
                    fileStore.deleteFile(recipeField.getPhoto().getStoreFileName());
                    photo = fileStore.storeFile(files.get().get(cnt));
                } catch (IOException e) {
                    throw new StoreFailException(e);
                }
                // 새로운 이미지 파일 세팅
                recipeField.setPhoto(photo);
                Photo.createPhoto(photo, recipeField);
                cnt++;
            }
        }
        // 추가된 recipeField 생성
        for (int i = recipeFields.size(); i < fieldForms.size(); i++) {
            create(id, fieldForms.get(i), files.get().get(cnt), member);
        }
    }

    @Transactional
    public void delete(Long id) {
        RecipeField recipeField = findOneById(id);
        recipeFieldRepository.delete(recipeField);
    }

}
