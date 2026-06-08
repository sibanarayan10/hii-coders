package com.sibanarayan.code.models.response;

import com.sibanarayan.code.enums.ProblemDifficulty;
import com.sibanarayan.code.enums.SubmissionStatus;
import lombok.*;

import java.time.Instant;
import java.util.UUID;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionResponse {
    private Instant createdAt;
    private String problemTitle;
    private ProblemDifficulty difficulty;
    private SubmissionStatus status;
}
