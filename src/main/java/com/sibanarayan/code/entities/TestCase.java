package com.sibanarayan.code.entities;

import com.sibanarayan.code.enums.StorageType;
import com.sibanarayan.code.models.embeddings.Block;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "test_cases", indexes = {
        @Index(name = "idx_testcase_problem_id", columnList = "problem_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestCase extends Base {

    @Column(name = "problem_id", nullable = false)
    private UUID problemId;

    @Column(name = "sequence_order", nullable = false)
    private Integer sequenceOrder;

    @Column(name = "hidden")
    private boolean hidden;

    @Column(name="display_input",columnDefinition = "jsonb")
    private List<Block> displayInput;

    @Column(name="display_output",columnDefinition = "jsonb")
    private List<Block> displayOutput;

    @Column(columnDefinition = "TEXT",name="expected_output")
    private String expectedOutput;

    @Column(name="input",columnDefinition = "TEXT")
    private String input;

    @Column(name="explanation",columnDefinition = "TEXT")
    private String explanation;


}
