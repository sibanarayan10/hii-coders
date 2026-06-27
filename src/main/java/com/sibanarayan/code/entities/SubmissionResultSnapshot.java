package com.sibanarayan.code.entities;

import com.sibanarayan.shared_package.enums.SubmissionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name="submission_result_snapshot")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionResultSnapshot {

    @Column(name="id")
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name="user_id")
    private UUID userId;

    @Column(name="submission_id")
    private UUID submissionId;

    @Column(name="problem_id")
    private UUID problemId;

    @Column(name="status")
    @Enumerated(EnumType.STRING)
    private SubmissionStatus status;

    @Column(name="created_at")
    private Instant occurredAt;

}
