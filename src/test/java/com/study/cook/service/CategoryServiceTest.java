package com.study.cook.service;

import com.study.cook.domain.Category;
import com.study.cook.repository.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Slf4j
class CategoryServiceTest {

    @Autowired
    CategoryService categoryService;

    @Autowired
    CategoryRepository categoryRepository;

    @BeforeEach
    void testSave() {
        Category category1 = new Category("한식");
        Category category2 = new Category("양식");
        Category category3 = new Category("일식");
        Category category4 = new Category("중식");

        categoryRepository.save(category1);
        categoryRepository.save(category2);
        categoryRepository.save(category3);
        categoryRepository.save(category4);
    }

    @Test
    @DisplayName("카테고리 생성")
    void create() {
        // given
        Category category = new Category("디저트");

        // when
        Long categoryId = categoryService.create(category);

        // then
        assertThat(categoryId).isEqualTo(category.getId());
    }

    @Test
    void findList() {
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
}