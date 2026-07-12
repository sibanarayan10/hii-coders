package com.sibanarayan.code.entities;

import com.sibanarayan.code.models.request.CreateProblemRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Example {

    private String input;

    private String output;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<CreateProblemRequest> blocks;
}
