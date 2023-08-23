package com.prograngers.backend.repository.solution;

import com.prograngers.backend.entity.QLikes;
import com.prograngers.backend.entity.QSolution;
import com.prograngers.backend.entity.Solution;
import com.prograngers.backend.entity.constants.AlgorithmConstant;
import com.prograngers.backend.entity.constants.DataStructureConstant;
import com.prograngers.backend.entity.constants.LanguageConstant;
import com.prograngers.backend.entity.constants.SortConstant;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.prograngers.backend.entity.QLikes.*;
import static com.prograngers.backend.entity.QSolution.*;
import static com.prograngers.backend.entity.constants.SortConstant.*;
import static org.springframework.util.ObjectUtils.isEmpty;

@RequiredArgsConstructor
@Repository
@Slf4j
public class QueryDslSolutionRepositoryImpl implements QueryDslSolutionRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public PageImpl<Solution> getSolutionList(
            Pageable pageable, Long problemId, LanguageConstant language,
            AlgorithmConstant algorithm, DataStructureConstant dataStructure, SortConstant sortBy) {
        List<Solution> result = null;

//        // pageable 사용 동적쿼리
//        result = jpaQueryFactory
//                .selectFrom(solution)
//                .where(solution.problem.id.eq(problemId), languageEq(language), algorithmEq(algorithm), dataStructureEq(dataStructure))
//                .orderBy(getOrderSpecifier(pageable.getSort()).stream().toArray(OrderSpecifier[]::new))
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize())
//                .fetch();

        if (sortBy.equals(NEWEST)){
            log.info("sortBy is NEWEST");
            result = jpaQueryFactory
                    .selectFrom(solution)
                    .where(solution.problem.id.eq(problemId), languageEq(language), algorithmEq(algorithm), dataStructureEq(dataStructure))
                    .orderBy(solution.date.desc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();


        } else if (sortBy.equals(LIKES)){
            log.info("sortBy is LIKES");
            result = jpaQueryFactory
                    .select(solution)
                    .from(likes)
                    .rightJoin(likes.solution, solution)
                    .where(solution.problem.id.eq(problemId), languageEq(language), algorithmEq(algorithm), dataStructureEq(dataStructure))
                    .groupBy(solution.id)
                    .orderBy(likes.id.count().desc(),solution.date.desc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();
        } else if (sortBy.equals(SCRAPS)){
            log.info("sortBy is SCRAPS");
//            result = jpaQueryFactory
//                    .select(solution)
//                    .from(solution)
//                    .where(solution.problem.id.eq(problemId), languageEq(language), algorithmEq(algorithm), dataStructureEq(dataStructure))
//                    .groupBy(solution.scrapSolution)
//                    .orderBy()
//                    . fetch();
        }

        Long count = jpaQueryFactory
                .select(solution.count())
                .from(solution)
                .where(solution.problem.id.eq(problemId), languageEq(language), algorithmEq(algorithm), dataStructureEq(dataStructure))
                .fetchOne();

        PageImpl<Solution> solutions = new PageImpl<>(result, pageable, count);
        return solutions;
    }

    private OrderSpecifier<?> sortByWhat(String sortBy) {
        if (sortBy.equals("newest")) {
            return solution.date.desc();
        }
        if (sortBy.equals("likes")) {
            // 서브쿼리
            // solution을 like의 수가 많은 대로 정렬하고 싶다
            JPAExpressions
                    .select(likes.count())
                    .from(likes)
                    .where(likes.solution.id.eq(solution.id))
                    .fetchOne();
            return null;
        }
        if (sortBy.equals("scraps")) {
            // 서브쿼리
            // solution을 scrap의 수가 많은 대로 정렬하고 싶다
            return null;
        } else return null;
    }

    private BooleanExpression dataStructureEq(DataStructureConstant dataStructure) {
        return dataStructure != null ? solution.dataStructure.eq(dataStructure) : null;
    }

    private BooleanExpression algorithmEq(AlgorithmConstant algorithm) {
        return algorithm != null ? solution.algorithm.eq(algorithm) : null;
    }

    private BooleanExpression languageEq(LanguageConstant language) {
        return language != null ? solution.language.eq(language) : null;
    }

    private List<OrderSpecifier> getOrderSpecifier(Sort sort){
        List<OrderSpecifier> orders = new ArrayList<>();
        sort.stream().forEach(order->{
            Order direction = order.isAscending()? Order.ASC : Order.DESC;
            String property = order.getProperty();
            PathBuilder orderByExpression = new PathBuilder(Solution.class,"solution");
            orders.add(new OrderSpecifier(direction,orderByExpression.get(property)));
        });
        return orders;
    }


}
