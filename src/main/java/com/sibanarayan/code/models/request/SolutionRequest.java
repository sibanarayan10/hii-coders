package com.sibanarayan.code.models.request;

import com.sibanarayan.shared_package.enums.ProgrammingLanguage;

import java.util.UUID;

public record SolutionRequest(
        UUID userId,
        UUID problemId,
        ProgrammingLanguage language,
        String solution) { }
