package com.sibanarayan.code.specifications;

import com.sibanarayan.code.entities.Problem;
import com.sibanarayan.code.enums.*;
import com.sibanarayan.code.models.request.ProblemFilterRequest;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.util.Set;

public class ProblemSpecification {

    public static Specification<Problem> withFilters(ProblemFilterRequest filter) {
        return Specification.allOf(
                isActive(),
                hasDifficulty(filter.getDifficulties()),
                hasCategory(filter.getCategories()),
                titleContains(filter.getSearch())
        );
    }

    private static Specification<Problem> isActive() {
        return (root, query, cb) ->
                cb.equal(root.get("recordStatus"), RecordStatus.ACTIVE);
    }

    private static Specification<Problem>
    hasDifficulty(Set<ProblemDifficulty> difficulties) {

        return difficulties == null || difficulties.isEmpty()
                ? null
                : (root, query, cb) ->
                root.get("difficulty").in(difficulties);
    }

    private static Specification<Problem>
    hasCategory(Set<ProblemsCategory> categories) {

        return categories == null || categories.isEmpty()
                ? null
                : (root, query, cb) -> {

            Join<Problem, ProblemsCategory> categoryJoin =
                    root.join("problem_categories");

            return categoryJoin
                    .get("category")
                    .in(categories);
        };
    }




    private static Specification<Problem> titleContains(String searchTerm) {
        return searchTerm == null ? null : (root, query, cb) ->
                cb.like(cb.lower(root.get("title")),
                        "%" + searchTerm.toLowerCase() + "%");
    }

    private static  Specification<Problem> hasCompanies(Set<Company> companies){

        return companies == null || companies.isEmpty()
                ? null
                : (root, query, cb) -> {

            Join<Problem, ProblemsCategory> categoryJoin =
                    root.join("problem_companies");

            return categoryJoin
                    .get("company")
                    .in(companies);
        };
    }
}