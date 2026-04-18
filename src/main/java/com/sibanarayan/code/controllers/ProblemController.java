package com.sibanarayan.code.controllers;

import com.sibanarayan.code.models.request.CreateProblemRequest;
import com.sibanarayan.code.models.request.ProblemFilterRequest;
import com.sibanarayan.code.models.response.ProblemResponse;
import com.sibanarayan.code.services.ProblemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/problems")
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemService problemService;

    @PostMapping
    public ResponseEntity<ProblemResponse> createProblem(
            @RequestBody @Valid CreateProblemRequest request,
            @RequestHeader("X-User-Id") UUID adminId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(problemService.createProblem(request, adminId));
    }

    @GetMapping
    public ResponseEntity<Page<ProblemResponse>> getProblems(
            @ModelAttribute ProblemFilterRequest filter) {
        return ResponseEntity.ok(problemService.getProblems(filter));
    }

    @GetMapping("/{problemId}")
    public ResponseEntity<ProblemResponse> getProblemById(@PathVariable UUID problemId) {
        return ResponseEntity.ok(problemService.getProblemById(problemId));
    }

    @DeleteMapping("/{problemId}")
    public ResponseEntity<Void> deleteProblem(@PathVariable UUID problemId) {
        problemService.deleteProblem(problemId);
        return ResponseEntity.noContent().build();
    }
}