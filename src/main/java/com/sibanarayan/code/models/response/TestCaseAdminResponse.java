package com.sibanarayan.code.models.response;

import com.sibanarayan.code.models.embeddings.Block;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Setter
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TestCaseAdminResponse {
    private UUID id;
    private List<Block> displayInput;
    private List<Block> displayOutput;
    private String input;
    private String output;
    private boolean hidden;
    private String explanation;
    private UUID problemId;
}
