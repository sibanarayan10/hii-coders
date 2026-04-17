package com.sibanarayan.code.models.response;

import com.sibanarayan.code.enums.ProblemDifficulty;
import com.sibanarayan.code.enums.ProblemsCategory;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
public class ProblemResponse {
    private UUID id;
    private String title;
    private String description;
    private ProblemDifficulty difficulty;
    private Set<ProblemsCategory> categories;
    private Instant createdAt;
}