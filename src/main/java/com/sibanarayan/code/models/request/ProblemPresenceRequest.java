package com.sibanarayan.code.models.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProblemPresenceRequest {

    /** The problem UUID the client is entering or leaving */
    private String problemId;
}
