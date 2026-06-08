package com.sibanarayan.code.models.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sibanarayan.code.enums.ProblemDifficulty;
import com.sibanarayan.code.enums.ProblemsCategory;
import com.sibanarayan.code.enums.ProgrammingLanguage;
import com.sibanarayan.code.enums.SolveStatus;
import com.sibanarayan.code.models.request.CreateProblemRequest;
import lombok.*;

import java.time.Instant;
import java.util.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProblemResponse {
    private UUID id;
    private String title;
    private ProblemDifficulty difficulty;
    private Set<ProblemsCategory> categories;
    private SolveStatus status;
    private List<CreateProblemRequest.Block> blocks;
    private Map<ProgrammingLanguage,String> solutionByLanguage;
    private Map<ProgrammingLanguage,String> ioByLanguage;

}