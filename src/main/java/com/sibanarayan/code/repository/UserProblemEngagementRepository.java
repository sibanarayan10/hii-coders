package com.sibanarayan.code.repository;

import com.sibanarayan.code.entities.UserProblemEngagement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserProblemEngagementRepository extends JpaRepository<UserProblemEngagement, UUID> {
    Optional<UserProblemEngagement> findByUserIdAndProblem_Id(UUID userId, UUID Id);
    Page<UserProblemEngagement> findAllByUserId(UUID userId, Pageable pageable);
}