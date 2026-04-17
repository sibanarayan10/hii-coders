package com.sibanarayan.code.models.request;

import com.sibanarayan.code.enums.ProblemDifficulty;
import com.sibanarayan.code.enums.ProblemsCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class CreateProblemRequest {

    @NotBlank
    @Size(max = 100)
    private String title;

    @NotBlank
    private String description;

    @NotNull
    private ProblemDifficulty difficulty;

    @NotEmpty
    private Set<ProblemsCategory> categories;
}
