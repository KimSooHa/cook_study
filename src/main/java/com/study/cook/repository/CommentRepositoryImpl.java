package com.study.cook.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.cook.dto.CommentDto;
import com.study.cook.dto.QCommentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import javax.persistence.EntityManager;
import java.util.List;

import static com.study.cook.domain.QComment.comment;
import static com.study.cook.domain.QMember.member;


public class CommentRepositoryImpl implements CommentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public CommentRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }



    /**
     * comment 목록
     */
    @Override
    public Page<CommentDto> findList(Long recipeId, Pageable pageable) {

        List<CommentDto> content = queryFactory
                .select(new QCommentDto(
                        comment.id,
                        comment.content,
                        comment.regDate,
                        comment.member.id,
                        comment.member.loginId
                ))
                .from(comment)
                .join(comment.member, member)
                .where(comment.recipe.id.eq(recipeId))
                .orderBy(comment.regDate.desc())
                .offset(pageable.getOffset())   // 시작 페이지
                .limit(pageable.getPageSize())  // 한 페이지당 몇개씩 가져올건지
                .fetch();// 컨텐츠만 반환

        // 카운트 쿼리
        JPAQuery<Long> countQuery = queryFactory
                .select(comment.count())
                .from(comment)
                .where(comment.recipe.id.eq(recipeId))
                .orderBy(comment.regDate.desc());

        return PageableExecutionUtils.getPage(content, pageable, () -> countQuery.fetchOne());

    }

}
