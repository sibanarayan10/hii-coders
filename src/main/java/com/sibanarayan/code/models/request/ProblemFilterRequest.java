package com.sibanarayan.code.models.request;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.sibanarayan.code.enums.Company;
import com.sibanarayan.code.enums.ProblemDifficulty;
import com.sibanarayan.code.enums.ProblemsCategory;
import com.sibanarayan.code.enums.SolveStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


import java.util.Set;

@AllArgsConstructor
@Getter
public class ProblemFilterRequest {
    private  Set<ProblemDifficulty> difficulties;
    private  Set<ProblemsCategory> categories;
    @Pattern(
            regexp = "createdAt|acceptanceRate",
            message = "Invalid sort request"
    )
    private  String sortBy="createdAt";
    @Pattern(
            regexp = "asc|desc",
            message = "Invalid order request"

    )
    private  String order="asc";
    private  String search;
    private  SolveStatus status;
    private  Set<Company> companies;
    @Min(0) private  int page = 1;
    @Min(1) @Max(50) private  int size = 10;
}
