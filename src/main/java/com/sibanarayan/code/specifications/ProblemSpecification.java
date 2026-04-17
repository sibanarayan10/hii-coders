package com.sibanarayan.code.specifications;

import com.sibanarayan.code.entities.Problem;
import com.sibanarayan.code.enums.ProblemDifficulty;
import com.sibanarayan.code.enums.ProblemsCategory;
import com.sibanarayan.code.enums.RecordStatus;
import com.sibanarayan.code.models.request.ProblemFilterRequest;
import org.springframework.data.jpa.domain.Specification;

public class ProblemSpecification {

    public static Specification<Problem> withFilters(ProblemFilterRequest filter) {
        return Specification
                .where(isActive())
                .and(hasDifficulty(filter.getDifficulty()))
                .and(hasCategory(filter.getCategory()))
                .and(hasCompanyTag(filter.getCompanyTag()))
                .and(titleContains(filter.getSearchTerm()));
    }

    private static Specification<Problem> isActive() {
        return (root, query, cb) ->
                cb.equal(root.get("record_status"), RecordStatus.ACTIVE);
    }

    private static Specification<Problem> hasDifficulty(ProblemDifficulty difficulty) {
        return difficulty == null ? null : (root, query, cb) ->
                cb.equal(root.get("difficulty"), difficulty);
    }

    private static Specification<Problem> hasCategory(ProblemsCategory category) {
        return category == null ? null : (root, query, cb) ->
                cb.isMember(category, root.get("categories"));
    }

    private static Specification<Problem> hasCompanyTag(String companyTag) {
        return companyTag == null ? null : (root, query, cb) ->
                cb.isMember(companyTag, root.get("companyTags"));
    }

    private static Specification<Problem> titleContains(String searchTerm) {
        return searchTerm == null ? null : (root, query, cb) ->
                cb.like(cb.lower(root.get("title")),
                        "%" + searchTerm.toLowerCase() + "%");
    }
}