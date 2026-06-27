package com.sibanarayan.code.models.response;

import com.sibanarayan.code.enums.ProblemDifficulty;
import com.sibanarayan.shared_package.enums.SubmissionStatus;
import lombok.*;

import java.time.Instant;


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
