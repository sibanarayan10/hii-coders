package com.sibanarayan.code.events;

import com.sibanarayan.code.enums.ProblemEventType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
public class ProblemEvent {
    private UUID problemId;
    private String title;
    private ProblemEventType eventType;
    private Instant occurredAt;
}