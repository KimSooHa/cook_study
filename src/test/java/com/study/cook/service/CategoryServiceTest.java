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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@Slf4j
class CategoryServiceTest {

    @Autowired
    CategoryService categoryService;

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
    @DisplayName("카테고리 목록 조회")
    void findList() {
        // when
        List<Category> list = categoryService.findList();

        // then
        assertThat(list.size()).isEqualTo(5);
    }

    @Test
    @DisplayName("아이디로 조회")
    void findOneById() {
        // given
        Category category = new Category("디저트");
        Long categoryId = categoryService.create(category);

        // when
        Category findCategory = categoryService.findOneById(categoryId);

        // then
        assertThat(findCategory.getName()).isEqualTo(category.getName());
    }

    @Test
    @DisplayName("카테고리 수정")
    void update() {
        // given
        Category category = new Category("디저트");
        Long categoryId = categoryService.create(category);
        String name = category.getName();

        // when
        categoryService.update(categoryId, "음료");

        // then
        assertThat(category.getName()).isNotEqualTo(name);
    }

    @Test
    @DisplayName("카테고리 삭제")
    void delete() {
        // given
        Category category = new Category("디저트");
        Long categoryId = categoryService.create(category);

        // when
        categoryService.delete(category);

        // then
        assertThatThrownBy(() -> categoryService.findOneById(categoryId)).isInstanceOf(IllegalArgumentException.class);
    }
}