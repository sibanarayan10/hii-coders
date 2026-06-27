package com.sibanarayan.code.entities;

import com.sibanarayan.shared_package.enums.ProgrammingLanguage;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;


import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Solution extends Base {

    @Column(name ="user_id",nullable = false,updatable = false)
    private UUID userId;

    @Column(name ="problem_id",nullable = false,updatable = false)
    private UUID problemId;

    @Column(name ="language",nullable = false,updatable = false)
    @Enumerated(EnumType.STRING)
    private ProgrammingLanguage language;

    @Column(name ="solution")
    private String solution;
}
