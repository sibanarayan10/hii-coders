package com.sibanarayan.code.models.response;

import com.sibanarayan.code.enums.ProblemDifficulty;
import com.sibanarayan.code.enums.ProblemsCategory;
import com.sibanarayan.code.enums.SolveStatus;
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
    private ProblemDifficulty difficulty;
    private Set<ProblemsCategory> category;
    private boolean saved;
    private boolean liked;
    private SolveStatus status;
    private String description;
    private Instant createdAt;
}