package com.sibanarayan.code.models.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PresenceResponse {

    /** Total number of authenticated users connected right now */
    private int onlineCount;

    /** Map of problemId → viewer count for every problem with active viewers */
    private Map<String, Integer> problemViewerCounts;
}
