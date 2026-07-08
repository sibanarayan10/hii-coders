package com.sibanarayan.code.entities;

import com.sibanarayan.code.enums.Company;
import com.sibanarayan.code.enums.ProblemDifficulty;
import com.sibanarayan.code.enums.ProblemsCategory;
import com.sibanarayan.shared_package.enums.ProgrammingLanguage;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import com.sibanarayan.code.models.request.CreateProblemRequest;

import java.util.*;

@Entity
@Table(name="problems",indexes = {
        @Index(name = "idx_problems_difficulty", columnList = "difficulty")
})
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Problem extends Base {

    private String title;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "blocks", columnDefinition = "jsonb")
    private List<CreateProblemRequest.Block> blocks;

    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private ProblemDifficulty difficulty;

    @ElementCollection(targetClass = ProblemsCategory.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "problem_categories", joinColumns = @JoinColumn(name = "problem_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private Set<ProblemsCategory> categories = new HashSet<>();

    @ElementCollection(targetClass = Company.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "problem_companies", joinColumns = @JoinColumn(name = "problem_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "company")
    private Set<Company> companies = new HashSet<>();

    @Column(name="created_by",nullable = false,updatable = false)
    private UUID createdBy;
//
//    @ElementCollection(fetch = FetchType.EAGER)
//    @CollectionTable(
//            name = "problem_examples",
//            joinColumns = @JoinColumn(name = "problem_id")
//    )
//    private Set<Example> example;

    @Column(name="runtime_ms")
    private Integer runtimeMs;

    @Column(name="memory")
    private Integer memory;

    @Column(name="solution_by_language",columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<ProgrammingLanguage,String> solutionsByLanguage=new HashMap<>();

    @Column(name="io_by_language",columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<ProgrammingLanguage,String> ioByLanguage=new HashMap<>();

    @Column(name="order_no")
    private Integer order;

    @Column(name="total_likes")
    private Integer totalLikes;

    @Column(name="total_dislikes")
    private Integer totalDislikes;



}
