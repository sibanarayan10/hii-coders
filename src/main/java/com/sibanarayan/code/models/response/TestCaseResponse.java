package com.sibanarayan.code.models.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class TestCaseResponse {
    private UUID id;
    private String inputData;
    private String expectedOutput;
    private boolean sample;
    private int sequenceOrder;
}
