package com.sibanarayan.code.events;

import com.sibanarayan.code.enums.EventType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Builder
@Setter
@Getter
public class UserEvent {
    private UUID id;
    private String email;
    private Instant occurredAt;
    private EventType eventType;
    private  String name;

}
