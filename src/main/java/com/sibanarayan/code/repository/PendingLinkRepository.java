package com.sibanarayan.code.repository;

import com.sibanarayan.code.entities.PendingLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PendingLinkRepository extends JpaRepository<PendingLink, UUID> {

    Optional<PendingLink> findByToken(String token);

    Optional<PendingLink> findByEmail(String email);

    void deleteByToken(String token);

    void deleteByEmail(String email);

    // For cleanup job — find all expired tokens
    List<PendingLink> findAllByExpiresAtBefore(LocalDateTime now);
}