package com.sibanarayan.code.entities;

import com.sibanarayan.code.enums.StorageType;
import jakarta.persistence.*;
import lombok.*;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    @Column(columnDefinition = "TEXT",name="input_data")
    private String inputData;

    @Column(columnDefinition = "TEXT",name="expected_output")
    private String expectedOutput;

    @Column(name = "input_file_key")
    private String inputFileKey;

    @Column(name = "output_file_key")
    private String outputFileKey;

    @Column(name = "is_sample", nullable = false)
    private boolean sample;

    @Column(name = "sequence_order", nullable = false)
    private Integer sequenceOrder;

    @Enumerated(EnumType.STRING)
    @Column(name = "storage_type", nullable = false)
    private StorageType storageType;

}
