package com.sibanarayan.code.consumers;

import com.sibanarayan.code.entities.Problem;
import com.sibanarayan.code.entities.SubmissionResultSnapshot;

import com.sibanarayan.code.entities.UserProblemEngagement;
import com.sibanarayan.code.enums.SolveStatus;
import com.sibanarayan.code.repository.ProblemRepository;
import com.sibanarayan.code.repository.SubmissionResultSnapshotRepository;
import com.sibanarayan.code.repository.UserProblemEngagementRepository;
import com.sibanarayan.shared_package.enums.RecordStatus;
import com.sibanarayan.shared_package.enums.SubmissionStatus;
import com.sibanarayan.shared_package.events.SubmissionResultEvent;
import com.sibanarayan.shared_package.exceptions.EntityAlreadyExistException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
@Component
public class SubmissionResultConsumer {

    private final SubmissionResultSnapshotRepository submissionResultSnapshotRepository;
    private final UserProblemEngagementRepository userProblemEngagementRepository;
    private final ProblemRepository problemRepository;

    @KafkaListener(
            topics = "submission.result",
            groupId = "submission-result",
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


        SolveStatus status=snapshot.getStatus()==SubmissionStatus.ACCEPTED?SolveStatus.SOLVED:SolveStatus.ATTEMPTED;

        Optional<UserProblemEngagement> wrapper=userProblemEngagementRepository.findByUserIdAndProblem_Id(userId,problemId);
        Optional<Problem> problemWrapper=problemRepository.findByIdAndRecordStatus(problemId, RecordStatus.ACTIVE);

        UserProblemEngagement upe=null;
        if(wrapper.isEmpty()){
            upe= UserProblemEngagement.builder()
                    .liked(false)
                    .saved(false)
                    .favorite(false)
                    .userId(userId)
                    .solveStatus(status)
                    .problem(problemWrapper.orElse(null))
                    .build();
        }else{
            upe =wrapper.get();
            upe.setSolveStatus(status);
        }

        userProblemEngagementRepository.save(upe);
        log.info("Problem status for user updated to {}",status);

    }
}
