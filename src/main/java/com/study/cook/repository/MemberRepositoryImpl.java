package com.study.cook.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.cook.dto.MemberDto;
import com.study.cook.dto.MemberLoginIdSearchCondition;
import com.study.cook.dto.MemberPwdSearchCondition;
import com.study.cook.dto.QMemberDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;


import javax.persistence.EntityManager;
import java.util.List;

import static com.study.cook.domain.QMember.member;


public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public MemberRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }


//    @Override
//    public List<MemberDto> search(MemberSearchCondition condition) {
//        return queryFactory
//                .select(new QMeber(
//                        member.id,
//                        member.username,
//                        member.age,
//                        team.id,
//                        team.name))
//                .from(member)
//                .leftJoin(member.team, team)
//                .where(usernameEq(condition.getUsername()),
//                        teamNameEq(condition.getTeamName()),
//                        ageGoe(condition.getAgeGoe()),
//                        ageLoe(condition.getAgeLoe()))
//                .fetch();
//    }

    /**
     * 단순한 페이징, fetchResults() 사용
     */

//    @Override
//    public Page<MemberTeamDto> searchPageSimple(MemberSearchCondition condition, Pageable pageable) {
//        QueryResults<MemberTeamDto> results = queryFactory
//                .select(new QMemberTeamDto(
//                        member.id,
//                        member.username,
//                        member.age,
//                        team.id,
//                        team.name))
//                .from(member)
//                .leftJoin(member.team, team)
//                .where(usernameEq(condition.getUsername()),
//                        teamNameEq(condition.getTeamName()),
//                        ageGoe(condition.getAgeGoe()),
//                        ageLoe(condition.getAgeLoe()))
//                .offset(pageable.getOffset())   // 시작 페이지
//                .limit(pageable.getPageSize())  // 한 페이지당 몇개씩 가져올건지
//                .fetchResults();    // 컨텐츠용 쿼리, 카운트용 쿼리
//
//        List<MemberTeamDto> content = results.getResults(); // 실제 데이터
//        long total = results.getTotal();    // totalCount
//
//        return new PageImpl<>(content, pageable, total);
//    }


    /**
     * 복잡한 페이징
     * 데이터 조회 쿼리와, 전체 카운트 쿼리를 분리
     */
//    @Override
//    public Page<MemberTeamDto> searchPageComplex(MemberSearchCondition condition, Pageable pageable) {
//        List<MemberTeamDto> content = queryFactory
//                .select(new QMemberTeamDto(
//                        member.id,
//                        member.username,
//                        member.age,
//                        team.id,
//                        team.name))
//                .from(member)
//                .leftJoin(member.team, team)
//                .where(usernameEq(condition.getUsername()),
//                        teamNameEq(condition.getTeamName()),
//                        ageGoe(condition.getAgeGoe()),
//                        ageLoe(condition.getAgeLoe()))
//                .offset(pageable.getOffset())   // 시작 페이지
//                .limit(pageable.getPageSize())  // 한 페이지당 몇개씩 가져올건지
//                .fetch();    // 컨텐츠만 반환
//
//        // 카운트 쿼리
//        JPAQuery<Member> countQuery = queryFactory
//                .select(member)
//                .from(member)
//                .leftJoin(member.team, team)
//                .where(usernameEq(condition.getUsername()),
//                        teamNameEq(condition.getTeamName()),
//                        ageGoe(condition.getAgeGoe()),
//                        ageLoe(condition.getAgeLoe()));
////                .fetchCount();
//
////       return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchCount);
//        return PageableExecutionUtils.getPage(content, pageable, () -> countQuery.fetchCount());    // 카운트쿼리가 필요하면 날리고, 필요없으면 안날리게 됨
////        return new PageImpl<>(content, pageable, total);
//    }

//    private BooleanExpression usernameEq(String username) {
//        return StringUtils.hasText(username) ? member.username.eq(username) : null;
//    }

//    private BooleanExpression teamNameEq(String teamName) {
//        return StringUtils.hasText(teamName) ? team.name.eq(teamName) : null;
//    }

//    private BooleanExpression ageGoe(Integer ageGoe) {
//        return ageGoe != null ? member.age.goe(ageGoe) : null;
//    }
//
//    private BooleanExpression ageLoe(Integer ageLoe) {
//        return ageLoe != null ? member.age.loe(ageLoe) : null;
//    }


}
