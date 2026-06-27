package com.sibanarayan.code.repository;

import com.sibanarayan.code.entities.Solution;
import com.sibanarayan.shared_package.enums.ProgrammingLanguage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface SolutionRepository extends JpaRepository<Solution, UUID> {
    Optional<Solution> findByUserIdAndProblemIdAndLanguage(UUID userId, UUID problemId, ProgrammingLanguage language);
}
