package com.study.cook.service;

import com.study.cook.SessionConst;
import com.study.cook.controller.CommentForm;
import com.study.cook.controller.RecipeForm;
import com.study.cook.domain.*;
import com.study.cook.dto.CommentDto;
import com.study.cook.exception.FindRecipeException;
import com.study.cook.exception.StoreFailException;
import com.study.cook.file.FileStore;
import com.study.cook.repository.CategoryRepository;
import com.study.cook.repository.CommentRepository;
import com.study.cook.repository.MemberRepository;
import com.study.cook.repository.RecipeRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Slf4j
class CommentServiceTest {

    @Autowired
    CommentService commentService;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    RecipeRepository recipeRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    FileStore fileStore;

    private String filePath = "/Users/sooha/Desktop/image/food/";
    private String fileName = "pizza_img.jpeg";
    private Long recipeId;

    @BeforeEach
    public void testSave() {
        Member member = new Member("testMember1", "test1", "testMember1234*", "testMember1@email.com", "010-1234-1231");
        memberRepository.save(member);

        RecipeForm form = setRecipeForm();
        recipeId = recipeSave(member, form, fileName, filePath);
    }

    @AfterEach
    public void testDelete() {
        deleteFile(recipeId);
    }

    @Test
    @DisplayName("레시피 댓글 생성")
    void create() {
        // given
        MockHttpSession session = setSession();
        CommentForm form = new CommentForm();
        form.setContent("test");
        form.setRecipeId(recipeId);

        // when
        Long commentId = commentService.create(form, session);

        // then
        assertThat(commentRepository.findById(commentId)).isPresent();
        assertThat(commentRepository.findById(commentId).get().getContent()).isEqualTo("test");
    }

    @Test
    @DisplayName("레시피 댓글 목록 조회")
    void findList() {
        // given
        Long commentId = save();
        PageRequest pageRequest = PageRequest.of(0, 4, Sort.Direction.DESC, "regDate");

        // when
        Page<CommentDto> list = commentService.findList(recipeId, pageRequest);

        // then
        assertThat(list.get().count()).isEqualTo(1);
        assertThat(list.toList().get(0).getCommentId()).isEqualTo(commentId);
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

    private MockHttpSession setSession() {
        MockHttpSession session = new MockHttpSession();
        Member member = memberRepository.findByLoginId("test1").get();
        session.setAttribute(SessionConst.LOGIN_MEMBER, member);
        return session;
    }

    private Long save() {
        Comment comment = new Comment("test");
        Recipe recipe = recipeRepository.findById(recipeId).get();

        Member member = memberRepository.findByLoginId("test1").get();
        Comment createdComment = Comment.createComment(comment, member, recipe);
        commentRepository.save(createdComment);

        return comment.getId();
    }

    private RecipeForm setRecipeForm() {
        RecipeForm form = new RecipeForm();
        form.setTitle("피자 만들기");
        form.setIntroduction("피자 만드는 레시피를 공유해요~");
        form.setIngredients("토마토 소스, 양파, 등 ..");
        form.setCategoryId(categoryRepository.findAll().get(1).getId());
        form.setServings(2);
        form.setCookingTime(2);
        return form;
    }

    private static MockMultipartFile getMockMultipartFile(String fileName, String filePath) throws IOException {
        return new MockMultipartFile(
                "image",
                fileName,
                "image/png",
                new FileInputStream(filePath + fileName));
    }

    private Long recipeSave(Member member, RecipeForm form, String fileName, String filePath) {
        MockMultipartFile file;
        Photo photo;
        try {
            file = getMockMultipartFile(fileName, filePath);
            photo = fileStore.storeFile(file);
        } catch (IOException e) {
            throw new StoreFailException("등록 실패: 이미지 파일 저장 실패", e);
        }

        Category category = categoryRepository.findById(form.getCategoryId()).get();
        Recipe recipe = new Recipe(form.getTitle(), form.getIntroduction(), photo, form.getIngredients(), form.getCookingTime(), form.getServings());
        Recipe createdRecipe = Recipe.createRecipe(recipe, member, category, photo);
        recipeRepository.save(createdRecipe);

        return recipe.getId();
    }


    private void deleteFile(Long recipeId) {
        String storeFileName = recipeRepository.findById(recipeId).get().getPhoto().getStoreFileName();
        fileStore.deleteFile(storeFileName);

        List<RecipeField> recipeFields = recipeRepository.findById(recipeId).get().getRecipeFields();
        if(!recipeFields.isEmpty()) {
            String storeFileFieldName = recipeFields.get(0).getPhoto().getStoreFileName();
            fileStore.deleteFile(storeFileFieldName);
        }
    }
}