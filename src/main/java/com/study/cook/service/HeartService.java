package com.study.cook.service;

import com.study.cook.domain.Category;
import com.study.cook.domain.Heart;
import com.study.cook.domain.Recipe;
import com.study.cook.repository.CategoryRepository;
import com.study.cook.repository.HeartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HeartService {

    private final HeartRepository heartRepository;


    /**
     * 카테고리 등록
     */
    @Transactional  // 변경해야 하기 때문에 읽기, 쓰기가 가능해야 함
    public Long create(Heart heart) {
        heartRepository.save(heart);
        return heart.getId();
    }


    /**
     *좋아요 전체 조회
     */
    public List<Heart> findList() {
        return heartRepository.findAll();
    }

    /**
     * 카테고리 조회
     */
//    public Category findOneById(Long categoryId) {
//        return categoryRepository.findById(categoryId).orElse(null);
//    }

    /**
     * 좋아요 수 조회
     */
    public Long countByRecipe(Recipe recipe) {
        return heartRepository.countByRecipe(recipe.getId());
    }


    @Transactional
    public void delete(Heart heart) {
        heartRepository.delete(heart);
    }

}
