package com.sibanarayan.code.models.response;

import com.sibanarayan.code.enums.SolveStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
public class EngagementResponse {
    private UUID id;
    private UUID userId;
    private UUID problemId;
    private String problemTitle;
    private boolean liked;
    private boolean saved;
    private SolveStatus solveStatus;
    private String notes;
    private Instant updatedAt;
}
