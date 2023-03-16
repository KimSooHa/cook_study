package com.study.cook.service;

import com.study.cook.controller.CommentForm;
import com.study.cook.domain.Comment;
import com.study.cook.domain.Member;
import com.study.cook.domain.Recipe;
import com.study.cook.dto.CommentDto;
import com.study.cook.repository.ClubRepository;
import com.study.cook.repository.CommentRepository;
import com.study.cook.repository.RecipeRepository;
import com.study.cook.util.MemberFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final ClubRepository clubRepository;
    private final RecipeRepository recipeRepository;
    private final CommentRepository commentRepository;
    private final MemberService memberService;
    private final CategoryService categoryService;
    private final ReservationService reservationService;
    private final ParticipationService participationService;
    private final MemberFinder memberFinder;


    /**
     * 댓글 등록
     */
    @Transactional  // 변경해야 하기 때문에 읽기, 쓰기가 가능해야 함
    public Long create(CommentForm form, HttpSession session) {

        Comment comment = new Comment(form.getContent());
        Recipe recipe = recipeRepository.findById(form.getRecipeId()).orElseThrow(() -> new IllegalArgumentException("댓글 쓰기 실패: 해당 레시피 글이 존재하지 않습니다."));

        Member member = memberFinder.getMember(session);
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

    }

    @Transactional
    public void delete(Long commentId) {
        Comment comment = findOneById(commentId);
        commentRepository.delete(comment);
    }
}
