package com.sibanarayan.code.repository;

import com.sibanarayan.code.entities.Problem;
import com.sibanarayan.code.enums.ProblemDifficulty;
import com.sibanarayan.code.enums.RecordStatus;
import com.sibanarayan.code.models.response.ProblemResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProblemRepository extends JpaRepository<Problem, UUID>, JpaSpecificationExecutor<Problem> {

    Page<Problem> findAllByRecordStatus(RecordStatus recordStatus, Pageable pageable);

    Page<Problem> findAllByDifficultyAndRecordStatus(
            ProblemDifficulty difficulty, RecordStatus recordStatus, Pageable pageable);

    Optional<Problem> findByIdAndRecordStatus(UUID problemId, RecordStatus status);

    @Query(value = """
            SELECT
                upe.is_saved AS saved,
                upe.is_liked AS liked,
                upe.solve_status AS status,
                pr.title AS title,
                ARRAY_AGG(pc.category) AS category,
                pr.id AS id
                      
            FROM public.problems pr
                      
            LEFT JOIN user_problem_engagement upe
                ON pr.id = upe.problem_id
                AND upe.user_id = :userId
                      
            LEFT JOIN problem_categories pc
                ON pr.id = pc.problem_id
                      
            WHERE pr.record_status = 'ACTIVE'
                      
            GROUP BY
                pr.id,
                upe.is_saved,
                upe.is_liked,
                upe.solve_status,
                pr.title;
            """,nativeQuery = true
    )
    Optional<List<ProblemResponse>> getProblems(UUID userId);

}