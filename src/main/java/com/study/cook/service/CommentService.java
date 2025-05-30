package com.study.cook.service;

import com.study.cook.controller.CommentForm;
import com.study.cook.domain.Comment;
import com.study.cook.domain.Member;
import com.study.cook.domain.Recipe;
import com.study.cook.dto.CommentDto;
import com.study.cook.exception.FindCommentException;
import com.study.cook.exception.FindRecipeException;
import com.study.cook.repository.CommentRepository;
import com.study.cook.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final RecipeRepository recipeRepository;
    private final CommentRepository commentRepository;


    /**
     * 댓글 등록
     */
    @Transactional  // 변경해야 하기 때문에 읽기, 쓰기가 가능해야 함
    @CacheEvict(value = "popularRecipeListCache", allEntries = true)  // 인기 레시피 리스트 캐시 제거
    public Long create(CommentForm form, Member member) {

        Comment comment = new Comment(form.getContent());
        Recipe recipe = recipeRepository.findById(form.getRecipeId()).orElseThrow(() -> new FindRecipeException("댓글 쓰기 실패: 해당 레시피 글이 존재하지 않습니다."));

        Comment createdComment = Comment.createComment(comment, member, recipe);
        commentRepository.save(createdComment);

        return comment.getId();
    }


    /**
     * 댓글 전체 조회
     */
    public Page<CommentDto> findList(Long recipeId, Pageable pageable) {
        return commentRepository.findList(recipeId, pageable);
    }


    /**
     * 댓글 조회
     */
    public Comment findOneById(Long commentId) {
        return commentRepository.findById(commentId).orElse(null);
    }


    @Transactional
    public void update(Long commentId, CommentForm form) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new FindCommentException("존재하지 않는 댓글입니다."));
        comment.setContent(form.getContent());
    }

    @Transactional
    @CacheEvict(value = "popularRecipeListCache", allEntries = true)  // 인기 레시피 리스트 캐시 제거
    public void delete(Long commentId) {
        Comment comment = findOneById(commentId);
        commentRepository.delete(comment);
    }
}
