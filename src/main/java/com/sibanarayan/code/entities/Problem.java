package com.sibanarayan.code.entities;

import com.sibanarayan.code.enums.ProblemDifficulty;
import com.sibanarayan.code.enums.ProblemsCategory;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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

    @Column(name="desc")
    private String description;

    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private ProblemDifficulty difficulty;

    @ElementCollection(targetClass = ProblemsCategory.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "problem_categories", joinColumns = @JoinColumn(name = "problem_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private Set<ProblemsCategory> categories = new HashSet<>();

    @Column(name="created_by",nullable = false,updatable = false)
    private UUID createdBy;


}
