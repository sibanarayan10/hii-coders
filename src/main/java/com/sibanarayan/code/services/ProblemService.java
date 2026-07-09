package com.sibanarayan.code.services;

import com.sibanarayan.code.enums.ProblemDifficulty;
import com.sibanarayan.code.models.request.CreateProblemRequest;
import com.sibanarayan.code.models.request.ProblemFilterRequest;
import com.sibanarayan.code.models.request.TestCaseRequest;
import com.sibanarayan.code.models.response.BaseProblemResponse;
import com.sibanarayan.code.models.response.ProblemResponse;
import com.sibanarayan.code.models.response.ProblemUserEngagementResponse;
import com.sibanarayan.code.models.response.TestCaseResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ProblemService {
    ProblemResponse createProblem(CreateProblemRequest request, UUID adminId);
    void deleteProblem(UUID problemId);
    Page<BaseProblemResponse> getProblems(ProblemFilterRequest filter,HttpServletRequest request);
    ProblemUserEngagementResponse getProblemUserEngagementDetail(UUID problemId, UUID userId);
    List<TestCaseResponse> getTestCasesByProblemId(UUID problemId);
    List<TestCaseResponse> getAllByProblemId(UUID problemId);
    Boolean createTestCase(TestCaseRequest request);
    ProblemResponse getProblem (UUID problemId);
    Page<BaseProblemResponse> getProblemsForAdmin(ProblemFilterRequest filter,HttpServletRequest request);
    Map<ProblemDifficulty,Integer> getProblemsCountByDifficulty();
    boolean toggleLike(UUID problemId,UUID userId);
}
