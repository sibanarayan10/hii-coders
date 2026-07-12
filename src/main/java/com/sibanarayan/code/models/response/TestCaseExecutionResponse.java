package com.sibanarayan.code.models.response;


import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TestCaseExecutionResponse {
    private UUID id;
    private UUID problemId;
    private String input;
    private String output;
}
