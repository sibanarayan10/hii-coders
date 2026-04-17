package com.sibanarayan.code.models.request;

import com.sibanarayan.code.enums.ProblemDifficulty;
import com.sibanarayan.code.enums.ProblemsCategory;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProblemFilterRequest {
    private ProblemDifficulty difficulty;
    private ProblemsCategory category;
    private String searchTerm;        // matches against title
    private String companyTag;
    @Min(0) private int page = 0;
    @Min(1) @Max(50) private int size = 20;
}
