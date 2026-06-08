package com.sibanarayan.code.repository;

import com.sibanarayan.code.entities.SubmissionResultSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SubmissionResultSnapshotRepository  extends JpaRepository<SubmissionResultSnapshot, UUID> {
    List<SubmissionResultSnapshot> findByUserId(UUID userId);
    boolean existsByUserIdAndSubmissionIdAndProblemId(UUID userId,UUID submissionId,UUID problemId);
}
