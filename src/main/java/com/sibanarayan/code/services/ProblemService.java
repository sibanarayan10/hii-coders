package com.sibanarayan.code.services;

import com.sibanarayan.code.models.request.CreateProblemRequest;
import com.sibanarayan.code.models.request.ProblemFilterRequest;
import com.sibanarayan.code.models.request.TestCaseRequest;
import com.sibanarayan.code.models.response.ProblemResponse;
import com.sibanarayan.code.models.response.TestCaseResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface ProblemService {
    ProblemResponse createProblem(CreateProblemRequest request, UUID adminId);
    void deleteProblem(UUID problemId);
    Page<ProblemResponse> getProblems(ProblemFilterRequest filter);
    ProblemResponse getProblemById(UUID problemId);
    List<TestCaseResponse> getTestCasesByProblemId(UUID problemId);
    Boolean createTestCase(TestCaseRequest request);
    Page<ProblemResponse> getSystemProblems(ProblemFilterRequest filter);
}
