package com.sibanarayan.code.repository;

import com.sibanarayan.code.entities.Problem;
import com.sibanarayan.code.enums.ProblemDifficulty;
import com.sibanarayan.code.enums.RecordStatus;
import com.sibanarayan.code.models.response.ProblemResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.UUID;

@Repository
public interface ProblemRepository extends JpaRepository<Problem, UUID>, JpaSpecificationExecutor<Problem> {

    Page<Problem> findAllByRecordStatus(RecordStatus recordStatus, Pageable pageable);

    Page<Problem> findAllByDifficultyAndRecordStatus(
            ProblemDifficulty difficulty, RecordStatus recordStatus, Pageable pageable);

}