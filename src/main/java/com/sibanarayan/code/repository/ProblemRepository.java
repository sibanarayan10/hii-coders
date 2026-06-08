package com.sibanarayan.code.repository;

import com.sibanarayan.code.entities.Problem;
import com.sibanarayan.code.enums.*;
import com.sibanarayan.code.models.response.ProblemResponse;
import com.sibanarayan.code.models.response.ProblemUserEngagementResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProblemRepository extends JpaRepository<Problem, UUID>, JpaSpecificationExecutor<Problem>, QuerydslPredicateExecutor<Problem> {

    Page<Problem> findAllByRecordStatus(RecordStatus recordStatus, Pageable pageable);

    Page<Problem> findAllByDifficultyAndRecordStatus(
            ProblemDifficulty difficulty, RecordStatus recordStatus, Pageable pageable);

    Optional<Problem> findByIdAndRecordStatus(UUID problemId, RecordStatus status);

    @Query(value = """
                SELECT
                   pr.id AS id,                       
                    pr.title AS title,
                    pr.difficulty AS difficulty,                        
                    ARRAY_REMOVE(
                        ARRAY_AGG(DISTINCT pc.category),
                        NULL
                    ) AS categories,                                                           
                    COALESCE(upe.solve_status, 'UNSOLVED') AS status, 
                    COALESCE(pr.blocks::text, '[]')
                        
                FROM problems pr
                LEFT JOIN user_problem_engagement upe
                    ON pr.id = upe.problem_id
                    AND upe.user_id = :userId
                LEFT JOIN problem_categories pc
                    ON pr.id = pc.problem_id
                WHERE pr.record_status = 'ACTIVE'
                    AND (:difficultiesSize = 0 OR pr.difficulty = ANY(CAST(:difficulties AS text[])))
                    AND (:categoriesSize = 0 OR EXISTS (
                        SELECT 1 FROM problem_categories pc2
                        WHERE pc2.problem_id = pr.id
                        AND pc2.category = ANY(CAST(:categories AS text[]))
                    ))
                    AND (:statusesSize = 0 OR COALESCE(upe.solve_status, 'UNSOLVED') = ANY(CAST(:statuses AS text[])))
                    AND (:search IS NULL OR LOWER(pr.title) LIKE LOWER(CONCAT('%', :search, '%')))
                GROUP BY pr.id, pr.title, pr.difficulty, pr.blocks, upe.solve_status
            """,
            countQuery = """
    SELECT COUNT(DISTINCT pr.id)
    
    FROM problems pr
    
    LEFT JOIN user_problem_engagement upe
        ON pr.id = upe.problem_id
        AND upe.user_id = :userId
    
    WHERE pr.record_status = 'ACTIVE'
        AND (:difficultiesSize = 0 
             OR pr.difficulty = ANY(CAST(:difficulties AS text[])))
        AND (:categoriesSize = 0 
             OR EXISTS (
                SELECT 1 FROM problem_categories pc2
                WHERE pc2.problem_id = pr.id
                AND pc2.category = ANY(CAST(:categories AS text[]))
             ))
        AND (:statusesSize = 0 
             OR COALESCE(upe.solve_status, 'UNSOLVED') = ANY(CAST(:statuses AS text[])))
        AND (:search IS NULL 
             OR LOWER(pr.title) LIKE LOWER(CONCAT('%', :search, '%')))
    """,
            nativeQuery = true)
    Page<ProblemResponse> getProblems(
            @Param("userId") UUID userId,
            @Param("difficulties") String[] difficulties,
            @Param("difficultiesSize") int difficultiesSize,
            @Param("categories") String[] categories,
            @Param("categoriesSize") int categoriesSize,
            @Param("search") String search,
            @Param("statuses") String[] statuses,
            @Param("statusesSize") int statusesSize,
            Pageable pageable
    );

    @Query(value = """
            SELECT
               
                  pr.title AS title,
                  pr.blocks AS blocks,
                  upe.is_saved AS saved,
                  upe.is_liked AS liked,
                  upe.is_favorite AS favorite,
                  pr.id AS id,
                  pr.difficulty AS difficulty,
                  ARRAY_AGG(DISTINCT pc.category) AS categories
                  
                  FROM problems pr
                  
                  LEFT JOIN user_problem_engagement upe
                      ON pr.id = upe.problem_id
                      AND upe.user_id=:userId
                  
                  LEFT JOIN problem_categories pc
                      ON pc.problem_id = pr.id
                  
                  WHERE pr.id = :problemId
                      AND pr.record_status = 'ACTIVE'
                  
                  GROUP BY
                      pr.id,
                      upe.is_saved,
                      upe.is_liked,
                      upe.is_favorite,
                      upe.solve_status,
                      pr.title,
                      pr.blocks,
                      pr.difficulty;
            """,nativeQuery = true)
    ProblemUserEngagementResponse getProblemDetail(UUID problemId, UUID userId);

}