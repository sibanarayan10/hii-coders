package com.sibanarayan.code.consumers;

import com.sibanarayan.code.entities.SubmissionResultSnapshot;

import com.sibanarayan.code.repository.SubmissionResultSnapshotRepository;
import com.sibanarayan.shared_package.events.SubmissionResultEvent;
import com.sibanarayan.shared_package.exceptions.EntityAlreadyExistException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
@Component
public class SubmissionResultConsumer {

    private final SubmissionResultSnapshotRepository submissionResultSnapshotRepository;

    @KafkaListener(
            topics = "submission.result",
            groupId = "submission.result",
            containerFactory = "submissionEventFactory"
    )
    public void consume(SubmissionResultEvent event){
        UUID userId=event.getUserId(),
            problemId=event.getProblemId(),
            submissionId=event.getSubmissionId();

        boolean result=submissionResultSnapshotRepository.existsByUserIdAndSubmissionIdAndProblemId(userId,submissionId,problemId);

        if(result){
            throw new EntityAlreadyExistException("Submission snapshot already exist");
        }

        SubmissionResultSnapshot snapshot=SubmissionResultSnapshot.builder()
                .userId(userId)
                .submissionId(submissionId)
                .problemId(problemId)
                .status(event.getStatus())
                .occurredAt(Instant.now())
                .build();

        submissionResultSnapshotRepository.save(snapshot);

        log.info("Submission snapshot created with submission id {}",submissionId);
    }
}
