package com.sibanarayan.code.services;

import com.sibanarayan.code.enums.ProgrammingLanguage;
import com.sibanarayan.code.models.request.SolutionRequest;

import java.util.UUID;

public interface SolutionService {
    String getSolution(ProgrammingLanguage language, UUID userId,UUID problemId);
    boolean saveSolution(SolutionRequest request);
}
