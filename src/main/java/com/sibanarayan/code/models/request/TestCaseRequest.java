package com.sibanarayan.code.models.request;

import com.sibanarayan.code.enums.StorageType;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
public class TestCaseRequest {
    private UUID problemId;
    private String inputData;
    private String expectedOutput;
    private boolean sample;
    private int timeLimit;
    private int memoryLimit;
    private int sequenceOrder;
    private StorageType storageType;
    private String inputFileKey;
    private String outputFileKey;
}
