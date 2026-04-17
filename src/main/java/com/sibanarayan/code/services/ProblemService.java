package com.sibanarayan.code.services;

import com.sibanarayan.code.enums.ProblemDifficulty;
import com.sibanarayan.code.models.request.CreateProblemRequest;
import com.sibanarayan.code.models.request.ProblemFilterRequest;
import com.sibanarayan.code.models.response.ProblemResponse;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface ProblemService {
    ProblemResponse createProblem(CreateProblemRequest request, UUID adminId);
    void deleteProblem(UUID problemId);
    Page<ProblemResponse> getProblems(ProblemFilterRequest filter);
}
