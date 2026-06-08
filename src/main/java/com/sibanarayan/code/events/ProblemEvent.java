package com.sibanarayan.code.events;

import com.sibanarayan.code.enums.EventType;
import com.sibanarayan.code.enums.ProgrammingLanguage;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Builder
public class ProblemEvent {
    private UUID problemId;
    private String title;
    private EventType eventType;
    private Instant occurredAt;
    private Map<ProgrammingLanguage,String> ioByLanguage;
}