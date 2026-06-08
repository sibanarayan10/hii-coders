package com.sibanarayan.code.models.request;

import com.sibanarayan.code.enums.ProgrammingLanguage;

import java.util.UUID;

public record SolutionRequest(
        UUID userId,
        UUID problemId,
        ProgrammingLanguage language,
        String solution) { }
