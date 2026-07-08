package com.sibanarayan.code.models.response;

import com.sibanarayan.code.enums.ProblemDifficulty;
import com.sibanarayan.code.enums.ProblemsCategory;
import com.sibanarayan.code.enums.SolveStatus;
import com.sibanarayan.code.models.request.CreateProblemRequest;
import com.sibanarayan.shared_package.enums.ProgrammingLanguage;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.*;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ProblemResponse extends BaseProblemResponse {
    private SolveStatus status;
    private Map<ProgrammingLanguage,String> solutionByLanguage;
    private Map<ProgrammingLanguage,String> ioByLanguage;
}