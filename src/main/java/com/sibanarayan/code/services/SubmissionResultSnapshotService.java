package com.sibanarayan.code.services;

import com.sibanarayan.code.models.response.SubmissionResponse;
import com.sibanarayan.code.models.response.UserStatistics;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface SubmissionResultSnapshotService {
    UserStatistics computeDashboardStats(UUID userId);
    List<SubmissionResponse> getRecentSubmission(UUID userId);
}
