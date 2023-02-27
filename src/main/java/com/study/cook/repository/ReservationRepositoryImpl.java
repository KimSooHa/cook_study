package com.study.cook.repository;

import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DateTemplate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.cook.domain.Reservation;
import org.springframework.data.repository.query.Param;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.study.cook.domain.QReservation.reservation;


public class ReservationRepositoryImpl implements ReservationRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ReservationRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }


    @Override
    public Optional<List<Reservation>> findByMemberIdAndDateTimeGt(Long memberId, LocalDateTime today) {

        return Optional.ofNullable(queryFactory.selectFrom(reservation)
                .where(reservation.member.id.eq(memberId),
                        dateGoe(today))
                .fetch());
    }

    @Override
    public Optional<List<Reservation>> findByCookingRoomAndStartDate(Long cookingRoomId, @Param("date") String startDate) {

        StringTemplate formattedDate = getFormattedDate();

        return Optional.ofNullable(queryFactory.selectFrom(reservation)
                .where(formattedDate.eq(startDate),
                        reservation.cookingRoom.id.eq(cookingRoomId))
                .fetch());
    }

    private static StringTemplate getFormattedDate() {
        return Expressions.stringTemplate(

                "DATE_FORMAT({0}, {1})"
                , reservation.startDateTime
                , ConstantImpl.create("%Y-%m-%d"));
    }

    private BooleanExpression dateGoe(LocalDateTime dateTime) {
        return dateTime != null ? reservation.startDateTime.goe(dateTime) : null;
    }



}
