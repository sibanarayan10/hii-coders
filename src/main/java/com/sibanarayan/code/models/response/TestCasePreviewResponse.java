package com.sibanarayan.code.models.response;

import com.sibanarayan.code.models.embeddings.Block;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
public class TestCasePreviewResponse {
    private UUID id;
    private UUID problemId;
    private List<Block> displayInput;
    private List<Block> displayOutput;
    private String explanation;
}
