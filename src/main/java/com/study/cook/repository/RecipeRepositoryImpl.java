package com.study.cook.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.cook.dto.QRecipeListDto;
import com.study.cook.dto.RecipeListDto;
import com.study.cook.dto.SearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.util.List;

import static com.study.cook.domain.QRecipe.recipe;


public class RecipeRepositoryImpl implements RecipeRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public RecipeRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    /**
     * 등록한 recipe 목록
     */
    @Override
    public Page<RecipeListDto> findByMemberId(Long memberId, SearchCondition condition, Pageable pageable) {
        List<RecipeListDto> content = queryFactory
                .select(new QRecipeListDto(
                        recipe.id,
                        recipe.title,
                        recipe.photo))
                .from(recipe)
                .where(titleLike(condition.getTitle()),
                        categoryNameEq(condition.getCategoryName()),
                        recipe.member.id.eq(memberId))
                .orderBy(recipe.regDate.desc())
                .offset(pageable.getOffset())   // 시작 페이지
                .limit(pageable.getPageSize())  // 한 페이지당 몇개씩 가져올건지
                .fetch();// 컨텐츠만 반환

        // 카운트 쿼리
        JPAQuery<Long> countQuery = queryFactory
                .select(recipe.count())
                .from(recipe)
                .where(titleLike(condition.getTitle()),
                        categoryNameEq(condition.getCategoryName()),
                        recipe.member.id.eq(memberId))
                .orderBy(recipe.regDate.desc());

        return PageableExecutionUtils.getPage(content, pageable, () -> countQuery.fetchOne());
    }

    /**
     * recipe 목록
     */
    @Override
    public Page<RecipeListDto> findList(SearchCondition condition, Pageable pageable) {
        List<RecipeListDto> content = queryFactory
                .select(new QRecipeListDto(
                        recipe.id,
                        recipe.title,
                        recipe.photo))
                .from(recipe)
                .where(titleLike(condition.getTitle()),
                        categoryNameEq(condition.getCategoryName()))
                .orderBy(recipe.regDate.desc())
                .offset(pageable.getOffset())   // 시작 페이지
                .limit(pageable.getPageSize())  // 한 페이지당 몇개씩 가져올건지
                .fetch();// 컨텐츠만 반환

        // 카운트 쿼리
        JPAQuery<Long> countQuery = queryFactory
                .select(recipe.count())
                .from(recipe)
                .where(titleLike(condition.getTitle()),
                        categoryNameEq(condition.getCategoryName()))
                .orderBy(recipe.regDate.desc());

        return PageableExecutionUtils.getPage(content, pageable, () -> countQuery.fetchOne());
    }

    /**
     * length: 반환할 목록 갯수
     * recipe 목록
     */
    @Override
    public List<RecipeListDto> findList(int length) {

        List<RecipeListDto> content =
            queryFactory.select(new QRecipeListDto(
                            recipe.id,
                            recipe.title,
                            recipe.photo))
                    .from(recipe)
                    .orderBy(recipe.comments.size().desc(),
                            recipe.regDate.desc())
                    .offset(0)
                    .limit(length)
                    .fetch();

        return content;
    }

    private BooleanExpression categoryNameEq(String categoryName) {
        return StringUtils.hasText(categoryName) ? recipe.category.name.eq(categoryName) : null;
    }

    private BooleanExpression titleLike(String title) {
        return StringUtils.hasText(title) ? recipe.title.contains(title) : null;
    }

}
