package com.sibanarayan.code.services;

import com.sibanarayan.code.entities.TestCase;
import com.sibanarayan.code.models.request.TestCaseRequest;
import com.sibanarayan.code.models.response.TestCaseAdminResponse;
import com.sibanarayan.code.models.response.TestCaseExecutionResponse;
import com.sibanarayan.code.models.response.TestCasePreviewResponse;

import java.util.List;
import java.util.UUID;

public interface TestcaseService {
    List<TestCaseAdminResponse> getAllTestCaseByProblemIdForAdmin(UUID problemId);
    List<TestCasePreviewResponse> getAllTestCaseByProblemIdForPreview(UUID problemId);
    List<TestCaseExecutionResponse> getAllTestCaseByProblemIdForExecution(UUID problemId);
    TestCase getTestcaseById(UUID testcaseId);
    void deleteTestcase(UUID testcaseId);
    void updateTestcase(TestCaseRequest request);
    void createAllTestcase(List<TestCaseRequest> list,UUID problemId);

}
