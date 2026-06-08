package com.sibanarayan.code.events;

import com.sibanarayan.code.enums.SubmissionStatus;
import lombok.*;

import java.time.Instant;
import java.util.UUID;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionResultEvent {
    private UUID userId;
    private UUID problemId;
    private UUID submissionId;
    private SubmissionStatus status;
    private Instant occurredAt;
}
