package com.sibanarayan.code.models.response;

import com.sibanarayan.code.enums.ProblemDifficulty;
import com.sibanarayan.code.enums.ProblemsCategory;
import com.sibanarayan.code.models.request.CreateProblemRequest;
import lombok.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AdminProblemResponse {
    private String title;
    private List<CreateProblemRequest.Block> description;
    private UUID id;
    private ProblemDifficulty difficulty;
    private double acceptanceRate;
    private Set<ProblemsCategory> categories;
}
