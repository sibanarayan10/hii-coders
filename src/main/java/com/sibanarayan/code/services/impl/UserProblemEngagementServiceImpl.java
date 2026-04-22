package com.sibanarayan.code.services.impl;

import com.sibanarayan.code.entities.CustomTestCase;
import com.sibanarayan.code.entities.Problem;
import com.sibanarayan.code.entities.UserProblemEngagement;
import com.sibanarayan.code.enums.RecordStatus;
import com.sibanarayan.code.enums.SolveStatus;
import com.sibanarayan.code.exceptions.ResourceNotFoundException;
import com.sibanarayan.code.exceptions.UnauthorizedException;
import com.sibanarayan.code.models.request.CustomTestCaseRequest;
import com.sibanarayan.code.models.request.EngagementRequest;
import com.sibanarayan.code.models.response.CustomTestCaseResponse;
import com.sibanarayan.code.models.response.EngagementResponse;
import com.sibanarayan.code.repositories.CustomTestCaseRepository;
import com.sibanarayan.code.repository.ProblemRepository;
import com.sibanarayan.code.repository.UserProblemEngagementRepository;
import com.sibanarayan.code.services.UserProblemEngagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProblemEngagementServiceImpl implements UserProblemEngagementService {

    private final UserProblemEngagementRepository engagementRepository;
    private final CustomTestCaseRepository customTestCaseRepository;
    private final ProblemRepository problemRepository;

    @Override
    @Transactional
    public EngagementResponse getOrCreateEngagement(UUID userId, UUID problemId) {
        return engagementRepository
                .findByUserIdAndProblem_Id(userId, problemId)
                .map(this::mapToResponse)
                .orElseGet(() -> createEngagement(userId, problemId));
    }

    @Override
    @Transactional
    public EngagementResponse updateEngagement(UUID userId, UUID problemId,
                                               EngagementRequest request) {
        UserProblemEngagement engagement = getEngagement(userId, problemId);

        if (request.getLiked() != null) engagement.setLiked(request.getLiked());
        if (request.getSaved() != null) engagement.setSaved(request.getSaved());
        if (request.getFavorite() != null) engagement.setFavorite(request.getFavorite());

        return mapToResponse(engagementRepository.save(engagement));
    }

    @Override
    @Transactional
    public EngagementResponse updateSolveStatus(UUID userId, UUID problemId,
                                                SolveStatus status) {
        UserProblemEngagement engagement = getEngagement(userId, problemId);
        engagement.setSolveStatus(status);
        return mapToResponse(engagementRepository.save(engagement));
    }

    @Override
    public Page<EngagementResponse> getUserEngagements(UUID userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("updatedAt").descending());
        return engagementRepository.findAllByUserId(userId, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional
    public CustomTestCaseResponse addCustomTestCase(UUID userId, UUID problemId,
                                                    CustomTestCaseRequest request) {
        Problem snapshot = problemRepository.findById(problemId)
                .filter(s -> s.getRecordStatus() == RecordStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Problem not found"));

        CustomTestCase testCase = CustomTestCase.builder()
                .userId(userId)
                .problem(snapshot)
                .inputData(request.getInputData())
                .build();

        return mapToTestCaseResponse(customTestCaseRepository.save(testCase));
    }

    @Override
    public List<CustomTestCaseResponse> getCustomTestCases(UUID userId, UUID problemId) {
        return customTestCaseRepository
                .findAllByUserIdAndProblem_Id(userId, problemId)
                .stream()
                .map(this::mapToTestCaseResponse)
                .toList();
    }

    @Override
    @Transactional
    public void deleteCustomTestCase(UUID userId, UUID testCaseId) {
        CustomTestCase testCase = customTestCaseRepository.findById(testCaseId)
                .orElseThrow(() -> new ResourceNotFoundException("Test case not found"));

        if (!testCase.getUserId().equals(userId)) {
            throw new UnauthorizedException("You don't own this test case");
        }

        testCase.setRecordStatus(RecordStatus.DELETED);
        customTestCaseRepository.save(testCase);
    }

    private EngagementResponse createEngagement(UUID userId, UUID problemId) {
        Problem snapshot = problemRepository.findById(problemId)
                .filter(s -> s.getRecordStatus() == RecordStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Problem not found"));

        UserProblemEngagement engagement = UserProblemEngagement.builder()
                .userId(userId)
                .problem(snapshot)
                .liked(false)
                .saved(false)
                .solveStatus(SolveStatus.UNSOLVED)
                .build();

        return mapToResponse(engagementRepository.save(engagement));
    }

    private UserProblemEngagement getEngagement(UUID userId, UUID problemId) {
        return engagementRepository
                .findByUserIdAndProblem_Id(userId, problemId)
                .orElseThrow(() -> new ResourceNotFoundException("Engagement not found"));
    }

    private EngagementResponse mapToResponse(UserProblemEngagement e) {
        return EngagementResponse.builder()
                .id(e.getId())
                .userId(e.getUserId())
                .problemId(e.getProblem().getId())
                .problemTitle(e.getProblem().getTitle())
                .liked(e.isLiked())
                .saved(e.isSaved())
                .solveStatus(e.getSolveStatus())
                .updatedAt(e.getUpdatedAt())
                .build();
    }

    private CustomTestCaseResponse mapToTestCaseResponse(CustomTestCase tc) {
        return CustomTestCaseResponse.builder()
                .id(tc.getId())
                .inputData(tc.getInputData())
                .createdAt(tc.getCreatedAt())
                .build();
    }
}
