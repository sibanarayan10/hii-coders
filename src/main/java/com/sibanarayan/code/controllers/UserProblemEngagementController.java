package com.sibanarayan.code.controllers;

import com.sibanarayan.code.enums.SolveStatus;
import com.sibanarayan.code.models.request.CustomTestCaseRequest;
import com.sibanarayan.code.models.request.EngagementRequest;
import com.sibanarayan.code.models.response.CustomTestCaseResponse;
import com.sibanarayan.code.models.response.EngagementResponse;
import com.sibanarayan.code.services.UserProblemEngagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/engagements")
@RequiredArgsConstructor
public class UserProblemEngagementController {

    private final UserProblemEngagementService engagementService;

    @GetMapping("/problems/{problemId}")
    public ResponseEntity<EngagementResponse> getEngagement(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID problemId) {
        return ResponseEntity.ok(engagementService.getOrCreateEngagement(userId, problemId));
    }

    @PatchMapping("/problems/{problemId}")
    public ResponseEntity<EngagementResponse> updateEngagement(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID problemId,
            @RequestBody @Valid EngagementRequest request) {
        return ResponseEntity.ok(engagementService.updateEngagement(userId, problemId, request));
    }

    @PatchMapping("/problems/{problemId}/status")
    public ResponseEntity<EngagementResponse> updateSolveStatus(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID problemId,
            @RequestParam SolveStatus status) {
        return ResponseEntity.ok(engagementService.updateSolveStatus(userId, problemId, status));
    }

    @GetMapping
    public ResponseEntity<Page<EngagementResponse>> getUserEngagements(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(engagementService.getUserEngagements(userId, page, size));
    }

    @PostMapping("/problems/{problemId}/test-cases")
    public ResponseEntity<CustomTestCaseResponse> addCustomTestCase(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID problemId,
            @RequestBody @Valid CustomTestCaseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(engagementService.addCustomTestCase(userId, problemId, request));
    }

    @GetMapping("/problems/{problemId}/test-cases")
    public ResponseEntity<List<CustomTestCaseResponse>> getCustomTestCases(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID problemId) {
        return ResponseEntity.ok(engagementService.getCustomTestCases(userId, problemId));
    }

    @DeleteMapping("/test-cases/{testCaseId}")
    public ResponseEntity<Void> deleteCustomTestCase(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID testCaseId) {
        engagementService.deleteCustomTestCase(userId, testCaseId);
        return ResponseEntity.noContent().build();
    }
}
