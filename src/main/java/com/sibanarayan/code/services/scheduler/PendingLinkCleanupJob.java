package com.sibanarayan.code.services.scheduler;

import com.sibanarayan.code.entities.PendingLink;
import com.sibanarayan.code.repository.PendingLinkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PendingLinkCleanupJob {

    private final PendingLinkRepository pendingLinkRepo;

    // Runs every hour
    @Scheduled(fixedRate = 3600000)
    public void cleanupExpiredLinks() {
        List<PendingLink> expiredLinks = pendingLinkRepo
                .findAllByExpiresAtBefore(LocalDateTime.now());

        if (!expiredLinks.isEmpty()) {
            pendingLinkRepo.deleteAll(expiredLinks);
            log.info("Cleaned up {} expired pending links", expiredLinks.size());
        }
    }
}
