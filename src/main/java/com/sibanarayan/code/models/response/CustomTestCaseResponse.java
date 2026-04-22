package com.sibanarayan.code.models.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
public class CustomTestCaseResponse {
    private UUID id;
    private String inputData;
    private Instant createdAt;
}
