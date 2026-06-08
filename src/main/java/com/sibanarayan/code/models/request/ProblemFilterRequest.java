package com.sibanarayan.code.models.request;

import com.sibanarayan.code.enums.Company;
import com.sibanarayan.code.enums.ProblemDifficulty;
import com.sibanarayan.code.enums.ProblemsCategory;
import com.sibanarayan.code.enums.SolveStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class ProblemFilterRequest {
    private Set<ProblemDifficulty> difficulties;
    private Set<ProblemsCategory> categories;
    private String search;
    private SolveStatus status;
    private Set<Company> companies;
    @Min(0) private int page = 1;
    @Min(1) @Max(50) private int size = 10;
}
