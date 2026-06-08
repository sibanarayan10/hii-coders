package com.sibanarayan.code.models.request;

import com.sibanarayan.code.enums.ProblemDifficulty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

@Builder
@Setter
@Getter

public class AdminProblemPageFilter {
    private String search;
    private String sortBy;
    private String order="asc";
   @Min(0) private int page;
    @Min(10) @Max(50) private int size=10;
    private ProblemDifficulty difficulty;
}
