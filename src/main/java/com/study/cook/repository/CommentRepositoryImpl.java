package com.study.cook.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.cook.dto.CommentDto;
import com.study.cook.dto.QCommentDto;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

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
    public Slice<CommentDto> findList(Long recipeId, Long lastId, int size) {

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
                .where(
                        comment.recipe.id.eq(recipeId),
                        lastId != null ? comment.id.lt(lastId) : null
                )
                .orderBy(comment.id.desc()) // regDate 말고 id 추천
                .limit(size + 1)
                .fetch();

        boolean hasNext = content.size() > size;

        List<CommentDto> result = hasNext
                ? content.subList(0, size)
                : content;

        return new SliceImpl<>(result, PageRequest.of(0, size), hasNext);
    }

    /**
     * 레시피에 해당하는 댓글 갯수 조회
     * @param recipeId
     * @return
     */
    public long countByRecipe(Long recipeId) {
        return queryFactory
                .select(comment.count())
                .from(comment)
                .where(comment.recipe.id.eq(recipeId))
                .fetchOne();
    }

}
