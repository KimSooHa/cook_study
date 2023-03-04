package com.study.cook.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.cook.domain.QParticipation;
import com.study.cook.dto.ClubListDto;
import com.study.cook.dto.QClubListDto;
import com.study.cook.dto.SearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.util.List;

import static com.study.cook.domain.QClub.club;


public class ClubRepositoryImpl implements ClubRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ClubRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }


    // 회원이 참여하고 있는 쿡스터디 목록
    @Override
    public Page<ClubListDto> findByParticipant(Long memberId, Pageable pageable) {
        QParticipation participationSub = new QParticipation("participationSub");

        List<ClubListDto> content = queryFactory
                .select(new QClubListDto(
                        club.id,
                        club.name,
                        club.member.loginId))
                .from(club)
                .innerJoin(club, participationSub.club)
                .where(club.id.eq(
                        JPAExpressions.select(participationSub.club.id)
                                .from(participationSub)
                                .where(participationSub.member.id.eq(memberId))))
                .offset(pageable.getOffset())   // 시작 페이지
                .limit(pageable.getPageSize())  // 한 페이지당 몇개씩 가져올건지
                .fetch();// 컨텐츠만 반환

        // 카운트 쿼리
        JPAQuery<Long> countQuery = queryFactory
                .select(club.count())
                .from(club)
                .innerJoin(club, participationSub.club)
                .where(participationSub.member.id.eq(memberId));

        return PageableExecutionUtils.getPage(content, pageable, () -> countQuery.fetchOne());
    }

    // 쿡스터디 목록
    @Override
    public Page<ClubListDto> findList(SearchCondition condition, Pageable pageable) {

        List<ClubListDto> content = queryFactory
                .select(new QClubListDto(
                        club.id,
                        club.name,
                        club.member.loginId))
                .from(club)
                .where(titleLike(condition.getTitle()),
                        categoryNameEq(condition.getCategoryName()))
                .offset(pageable.getOffset())   // 시작 페이지
                .limit(pageable.getPageSize())  // 한 페이지당 몇개씩 가져올건지
                .fetch();// 컨텐츠만 반환

        // 카운트 쿼리
        JPAQuery<Long> countQuery = queryFactory
                .select(club.count())
                .from(club)
                .where(titleLike(condition.getTitle()),
                        categoryNameEq(condition.getCategoryName()));

        return PageableExecutionUtils.getPage(content, pageable, () -> countQuery.fetchOne());

    }

    @Override
    public List<ClubListDto> findList(int length) {

        List<ClubListDto> content = queryFactory.select(new QClubListDto(
                        club.id,
                        club.name,
                        club.member.loginId))
                .from(club)
                .orderBy(club.participations.size().desc())
                .offset(0)
                .limit(length)
                .fetch();

        return content;
    }

    private BooleanExpression titleLike(String title) {
        return StringUtils.hasText(title) ? club.name.contains(title) : null;
    }

    private BooleanExpression categoryNameEq(String categoryName) {
        return StringUtils.hasText(categoryName) ? club.category.name.eq(categoryName) : null;
    }


}
