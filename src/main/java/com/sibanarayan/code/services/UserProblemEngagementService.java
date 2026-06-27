package com.sibanarayan.code.services;

import com.sibanarayan.code.enums.SolveStatus;
import com.sibanarayan.code.models.request.CustomTestCaseRequest;
import com.sibanarayan.code.models.request.EngagementRequest;
import com.sibanarayan.code.models.response.CustomTestCaseResponse;
import com.sibanarayan.code.models.response.EngagementResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface UserProblemEngagementService {
    EngagementResponse getOrCreateEngagement(UUID userId, UUID problemId);
    EngagementResponse updateEngagement(UUID userId, UUID problemId, EngagementRequest request);
    EngagementResponse updateSolveStatus(UUID userId, UUID problemId, SolveStatus status);
    Page<EngagementResponse> getUserEngagements(UUID userId, int page, int size);
    CustomTestCaseResponse addCustomTestCase(UUID userId, UUID problemId, CustomTestCaseRequest request);
    List<CustomTestCaseResponse> getCustomTestCases(UUID userId, UUID problemId);
    void deleteCustomTestCase(UUID userId, UUID testCaseId);
}
