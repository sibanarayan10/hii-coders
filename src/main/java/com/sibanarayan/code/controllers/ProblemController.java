package com.sibanarayan.code.controllers;

import com.sibanarayan.code.customAnnotation.Role;
import com.sibanarayan.code.enums.ProblemDifficulty;
import com.sibanarayan.code.models.request.AdminProblemPageFilter;
import com.sibanarayan.code.models.request.CreateProblemRequest;
import com.sibanarayan.code.models.request.ProblemFilterRequest;
import com.sibanarayan.code.models.request.TestCaseRequest;
import com.sibanarayan.code.models.response.BaseProblemResponse;
import com.sibanarayan.code.models.response.ProblemResponse;
import com.sibanarayan.code.models.response.ProblemUserEngagementResponse;
import com.sibanarayan.code.models.response.TestCaseResponse;
import com.sibanarayan.code.services.ProblemService;
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

    @GetMapping("/{problemId}/testCases")
    public ResponseEntity<List<TestCaseResponse>> getTestCasesByProblem(@PathVariable UUID problemId) {
        return ResponseEntity.ok(problemService.getTestCasesByProblemId(problemId));
    }

    @GetMapping("/{problemId}/testCases/all")
    public ResponseEntity<List<TestCaseResponse>> getAllTestCasesByProblem(@PathVariable UUID problemId) {
        return ResponseEntity.ok(problemService.getAllByProblemId(problemId));
    }
    @PostMapping("/{problemId}/testCase")
    @Role("ADMIN")
    public ResponseEntity<Boolean> createTestCase(@RequestBody TestCaseRequest request) {
        return ResponseEntity.ok(problemService.createTestCase(request));
    }




}