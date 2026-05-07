package com.sibanarayan.code.repository;

import com.sibanarayan.code.entities.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TestCaseRepository extends JpaRepository<TestCase,UUID> {
    List<TestCase> getByProblemId(UUID problemId);
}



