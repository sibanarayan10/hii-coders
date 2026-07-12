package com.sibanarayan.code.controllers;

import com.sibanarayan.code.customAnnotation.Role;
import com.sibanarayan.code.enums.ProblemDifficulty;
import com.sibanarayan.code.models.request.*;
import com.sibanarayan.code.models.response.*;
import com.sibanarayan.code.services.ProblemService;
import com.sibanarayan.code.services.TestcaseService;
import com.sibanarayan.shared_package.security.JwtAuthFilter;
import com.sibanarayan.shared_package.security.JwtUtility;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/problems")
@RequiredArgsConstructor
@Slf4j
public class ProblemController {

    private final ProblemService problemService;
    private final JwtUtility jwtUtility;
    private final TestcaseService testcaseService;

    @PostMapping
    @Role("ADMIN")
    public ResponseEntity<ProblemResponse> createProblem(HttpServletRequest request,
            @RequestBody @Valid CreateProblemRequest problemRequest) {
        String token= jwtUtility.extractTokenFromCookie(request);
        UUID adminId=jwtUtility.getUserId(token);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(problemService.createProblem(problemRequest, adminId));
    }

    @GetMapping
    public ResponseEntity<Page<BaseProblemResponse>> getProblems(
            @ModelAttribute ProblemFilterRequest filter,HttpServletRequest request) {
        return ResponseEntity.ok(problemService.getProblems(filter,request));
    }

    @GetMapping("count-by-difficulty")
    public ResponseEntity<Map<ProblemDifficulty,Integer>> getProblemsCountByDifficulty(){
        return ResponseEntity.ok(problemService.getProblemsCountByDifficulty());
    }

    @GetMapping("system")
    @Role("ADMIN")
    public ResponseEntity<Page<BaseProblemResponse>> getProblemsForAdmin(@ModelAttribute ProblemFilterRequest filter,HttpServletRequest request){
        return ResponseEntity.ok(problemService.getProblemsForAdmin(filter,request));
    }

    @GetMapping("/{problemId}")
    public ResponseEntity<ProblemUserEngagementResponse> getProblemUserEngagementDetail(@PathVariable UUID problemId, HttpServletRequest request) {
        String token= jwtUtility.extractTokenFromCookie(request);
        UUID userId=token==null?null:jwtUtility.getUserId(token);
        return ResponseEntity.ok(problemService.getProblemUserEngagementDetail(problemId,userId));
    }

    @GetMapping("/{problemId}/detail")
    public ResponseEntity<ProblemResponse> getProblem(@PathVariable UUID problemId) {
        return ResponseEntity.ok(problemService.getProblem(problemId));
    }

    @PutMapping("/{problemId}")
    @Role("ADMIN")
    public ResponseEntity<Boolean> deleteProblem(@PathVariable UUID problemId) {
        problemService.deleteProblem(problemId);
        return ResponseEntity.ok(true);
    }



    @PutMapping("{problemId}/toggle-like")
    public ResponseEntity<Boolean> toggleLike(@PathVariable UUID problemId, HttpServletRequest request){
        String token=jwtUtility.extractTokenFromCookie(request);
        UUID userId=jwtUtility.getUserId(token);
        return ResponseEntity.ok(problemService.toggleLike(problemId,userId));
    }



    @GetMapping("/{problemId}/testCases/execution")
    public ResponseEntity<List<TestCaseExecutionResponse>> getAllTestCasesByProblemForExecution(@PathVariable UUID problemId) {
        return ResponseEntity.ok(testcaseService.getAllTestCaseByProblemIdForExecution(problemId));
    }

    @GetMapping("/{problemId}/testCases/admin")
    @Role("ADMIN")
    public ResponseEntity<List<TestCaseAdminResponse>> getAllTestCasesByProblemForAdmin(@PathVariable UUID problemId) {
        return ResponseEntity.ok(testcaseService.getAllTestCaseByProblemIdForAdmin(problemId));
    }

    @GetMapping("/{problemId}/testCases/preview")
    public ResponseEntity<List<TestCasePreviewResponse>> getAllTestCasesByProblemForPreview(@PathVariable UUID problemId) {
        return ResponseEntity.ok(testcaseService.getAllTestCaseByProblemIdForPreview(problemId));
    }
    @PostMapping("/{problemId}/testCases")
    @Role("ADMIN")
    public ResponseEntity<String> createTestCase(@RequestBody CreateTestcaseRequest request) {
        testcaseService.createAllTestcase(request.getTestCases(),request.getProblemId());
        return ResponseEntity.ok("All testcase created successfully");
    }

    @PutMapping("{problemId}/testCases/{testcaseId}")
    @Role("ADMIN")
    public ResponseEntity<String> updateTestcase(@RequestBody TestCaseRequest update){
        testcaseService.updateTestcase(update);
        return ResponseEntity.ok("Testcase updated successfully");
    }

    @PutMapping("{problemId}/testCases/{testcaseId}/delete")
    @Role("ADMIN")
    public ResponseEntity<String> deleteTestcase(@PathVariable UUID testcaseId){
        testcaseService.deleteTestcase(testcaseId);
        return ResponseEntity.ok("Testcase deleted successfully");
    }


}