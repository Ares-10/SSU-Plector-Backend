package ssuPlector.repository.developer;

import static ssuPlector.domain.QDeveloper.developer;
import static ssuPlector.domain.QProjectDeveloper.projectDeveloper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import ssuPlector.domain.Developer;
import ssuPlector.domain.category.DevLanguage;
import ssuPlector.domain.category.Part;
import ssuPlector.domain.category.TechStack;
import ssuPlector.dto.request.DeveloperDTO.DeveloperMatchingDTO;
import ssuPlector.global.exception.GlobalException;
import ssuPlector.global.response.code.GlobalErrorCode;

@RequiredArgsConstructor
public class DeveloperRepositoryImpl implements DeveloperRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Developer> findDevelopers(String sortType, Part part, Pageable pageable) {

        OrderSpecifier<?> orderSpecifiers = sortTypeEq(sortType);

        QueryResults<Developer> results =
                queryFactory
                        .select(developer)
                        .from(developer)
                        .where(part1Eq(part).or(part2Eq(part)))
                        .orderBy(orderSpecifiers)
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetchResults();

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }

    private OrderSpecifier<?> sortTypeEq(String sortType) {

        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

        if (sortType == null || sortType.isEmpty()) {
            orderSpecifiers.add(new OrderSpecifier<>(Order.DESC, developer.updatedDate));
        } else if (sortType.equals("recent")) {
            orderSpecifiers.add(new OrderSpecifier<>(Order.DESC, developer.updatedDate));
        } else if (sortType.equals("old")) {
            orderSpecifiers.add(new OrderSpecifier<>(Order.ASC, developer.updatedDate));
        } else if (sortType.equals("high")) {
            orderSpecifiers.add(new OrderSpecifier<>(Order.DESC, developer.hits));
        } else if (sortType.equals("low")) {
            orderSpecifiers.add(new OrderSpecifier<>(Order.ASC, developer.hits));
        } else {
            throw new GlobalException(GlobalErrorCode._BAD_REQUEST);
        }

        return orderSpecifiers.get(0);
    }

    BooleanExpression part1Eq(Part part) {
        return part != null ? developer.part1.eq(part) : Expressions.asBoolean(true).isTrue();
    }

    BooleanExpression part2Eq(Part part) {
        return part != null ? developer.part2.eq(part) : Expressions.asBoolean(true).isTrue();
    }

    public List<Developer> searchDeveloper(String name) {
        return queryFactory.selectFrom(developer).where(developer.name.contains(name)).fetch();
    }

    BooleanExpression searchDeveloperStudentNumber(Long min, Long max) {
        if (min == null) min = 0L;
        if (max == null) max = 100L;
        return developer.studentNumber.between(min.toString(), max.toString());
    }

    BooleanExpression searchDeveloperProjectExperience(Boolean experience) {
        if (experience == null || !experience) {
            return developer.isNotNull();
        }
        return queryFactory
                .selectFrom(projectDeveloper)
                .where(projectDeveloper.developer.eq(developer))
                .exists();
    }

    @Override
    public Map<Long, Double> matchDeveloper(String developerInfo, DeveloperMatchingDTO requestDTO) {
        // part, 개발 경험, 학번
        List<Developer> developers =
                queryFactory
                        .selectFrom(developer)
                        .where(
                                (part1Eq(requestDTO.getPart()).or(part2Eq(requestDTO.getPart())))
                                        .and(
                                                searchDeveloperStudentNumber(
                                                        requestDTO.getStudentNumberMin(),
                                                        requestDTO.getStudentNumberMax()))
                                        .and(
                                                searchDeveloperProjectExperience(
                                                        requestDTO.getProjectExperience())))
                        .fetch();
        if (developers.size() == 0) throw new GlobalException(GlobalErrorCode.DEVELOPER_NOT_FOUND);

        Map<Long, Double> weight = new HashMap<>();

        // 사용언어, 기술스택
        for (Developer developer : developers) {
            double tmpWeight = 0.0;

            List<DevLanguage> devLanguageList = developer.getLanguageList();
            List<DevLanguage> intersectionLang =
                    devLanguageList.stream()
                            .filter(requestDTO.getLanguageList()::contains)
                            .toList();

            tmpWeight += intersectionLang.size() * 0.5;

            List<TechStack> techStackList = developer.getTechStackList();
            List<TechStack> intersectionTech =
                    techStackList.stream().filter(requestDTO.getTechStackList()::contains).toList();

            tmpWeight += intersectionTech.size() * 0.3;

            weight.put(developer.getId(), tmpWeight);
        }
        return weight;
    }
}
