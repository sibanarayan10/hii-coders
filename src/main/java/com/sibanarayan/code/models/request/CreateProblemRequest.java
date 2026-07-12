package com.sibanarayan.code.models.request;

import com.sibanarayan.code.enums.ProblemDifficulty;
import com.sibanarayan.code.enums.ProblemsCategory;
import com.sibanarayan.code.models.embeddings.Block;
import com.sibanarayan.shared_package.enums.ProgrammingLanguage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class CreateProblemRequest {

    private UUID id;

    @NotBlank
    @Size(max = 100)
    private String title;

    @NotEmpty
    private List<Block> blocks;

    @NotNull
    private ProblemDifficulty difficulty;

    @NotEmpty
    private Set<ProblemsCategory> categories;

    private Map<ProgrammingLanguage,String> solutionByLanguage;

    private Map<ProgrammingLanguage,String> ioByLanguage;



}


