package com.sibanarayan.code.services.impl;

import com.sibanarayan.code.entities.Problem;
import com.sibanarayan.code.entities.Solution;
import com.sibanarayan.code.enums.ProgrammingLanguage;
import com.sibanarayan.code.enums.RecordStatus;
import com.sibanarayan.code.models.request.SolutionRequest;
import com.sibanarayan.code.repository.ProblemRepository;
import com.sibanarayan.code.repository.SolutionRepository;
import com.sibanarayan.code.services.SolutionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class SolutionServiceImpl implements SolutionService {
    private final SolutionRepository solutionRepository;
    private final ProblemRepository problemRepository;

    public String getSolution(ProgrammingLanguage language, UUID userId,UUID problemId){
        StringBuilder builder=new StringBuilder();
        solutionRepository.findByUserIdAndProblemIdAndLanguage(userId,problemId,language).ifPresentOrElse((sol)->{
            builder.append(sol.getSolution());
        },()->{
            Optional<Problem> exist=problemRepository.findByIdAndRecordStatus(problemId, RecordStatus.ACTIVE);
            if(exist.isPresent()){
                String solution=exist.get().getSolutionsByLanguage().getOrDefault(language,"");
                builder.append(solution);
            }
        });
        return builder.toString();
    }

    public boolean saveSolution(SolutionRequest request){
        UUID userId=request.userId(),problemId=request.problemId();
        ProgrammingLanguage language=request.language();

        solutionRepository.findByUserIdAndProblemIdAndLanguage(userId,problemId,language).ifPresentOrElse((sol)->{
            sol.setSolution(request.solution());
            solutionRepository.save(sol);
        },()->{
            Solution solution= Solution.builder()
                    .userId(userId)
                    .problemId(problemId)
                    .language(language)
                    .solution(request.solution())
                    .build();
            solutionRepository.save(solution);
        });

        return true;
    }
}
