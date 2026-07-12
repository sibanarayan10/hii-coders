package com.sibanarayan.code.models.response;

import com.sibanarayan.code.enums.ProblemDifficulty;
import com.sibanarayan.code.enums.ProblemsCategory;
import com.sibanarayan.code.models.embeddings.Block;
import com.sibanarayan.code.models.request.CreateProblemRequest;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Set;
import java.util.UUID;


@SuperBuilder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BaseProblemResponse {
    private String title;
    private List<Block> description;
    private UUID id;
    private ProblemDifficulty difficulty;
    private double acceptanceRate;
    private Set<ProblemsCategory> categories;
}
