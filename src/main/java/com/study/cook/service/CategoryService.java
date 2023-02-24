package com.study.cook.service;

import com.study.cook.domain.Category;
import com.study.cook.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;


    /**
     * 카테고리 등록
     */
    @Transactional  // 변경해야 하기 때문에 읽기, 쓰기가 가능해야 함
    public Long create(Category category) {
        categoryRepository.save(category);
        return category.getId();
    }


    /**
     *카테고리 전체 조회
     */
    public List<Category> findList() {
        return categoryRepository.findAll();
    }

    /**
     * 카테고리 조회
     */
    public Category findOneById(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(() -> new IllegalArgumentException("no such data"));
    }


    @Transactional
    public void update(Long id, String name) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("no such data"));
        category.setName(name);
    }

    @Transactional
    public void delete(Category category) {
        categoryRepository.delete(category);
    }

}
