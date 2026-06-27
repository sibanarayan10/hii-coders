package com.sibanarayan.code.repository;

import com.sibanarayan.code.entities.TestCase;
import com.sibanarayan.shared_package.enums.RecordStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TestCaseRepository extends JpaRepository<TestCase,UUID> {
    List<TestCase> getByProblemIdAndRecordStatus(UUID problemId, RecordStatus status);
    List<TestCase> getByProblemIdAndSampleAndRecordStatus(UUID problemId, boolean sample, RecordStatus status);
}



