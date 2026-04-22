package com.sibanarayan.code.repositories;

import com.sibanarayan.code.entities.CustomTestCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CustomTestCaseRepository extends JpaRepository<CustomTestCase, UUID> {
    List<CustomTestCase> findAllByUserIdAndProblem_Id(UUID userId, UUID problemId);
}
