package ssuPlector.repository.project;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import ssuPlector.domain.Project;
import ssuPlector.domain.QImage;
import ssuPlector.domain.QProject;
import ssuPlector.domain.category.Category;

@RequiredArgsConstructor
public class ProjectRepositoryCustomImpl implements ProjectRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Project> findProjects(
            String searchString, String category, String sortType, Pageable pageable) {

        QProject project = QProject.project;
        QImage image = QImage.image;

        JPAQuery<Project> query =
                queryFactory.selectFrom(project).leftJoin(project.imageList, image).fetchJoin();
        JPAQuery<Long> countQuery = queryFactory.selectFrom(project).select(Wildcard.count);

        if (searchString != null && !searchString.isBlank()) {
            query.where(project.name.containsIgnoreCase(searchString));
            countQuery.where(project.name.containsIgnoreCase(searchString));
        }

        if (category != null && !category.isBlank()) {
            query.where(project.category.eq(Category.valueOf(category)));
            countQuery.where(project.name.containsIgnoreCase(searchString));
        }

        if (sortType != null && !sortType.isBlank()) {
            if (sortType.equals("recent")) {
                query.orderBy(project.updatedDate.desc());
            } else if (sortType.equals("old")) {
                query.orderBy(project.updatedDate.asc());
            } else if (sortType.equals("high")) {
                query.orderBy(project.hits.desc());
            } else if (sortType.equals("low")) {
                query.orderBy(project.hits.asc());
            }
        }

        List<Project> content =
                query.offset(pageable.getOffset()).limit(pageable.getPageSize()).fetch();
        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }
}
