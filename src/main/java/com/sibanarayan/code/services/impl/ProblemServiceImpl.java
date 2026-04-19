package com.sibanarayan.code.services.impl;

import com.sibanarayan.code.entities.Problem;
import com.sibanarayan.code.enums.ProblemDifficulty;
import com.sibanarayan.code.enums.ProblemEventType;
import com.sibanarayan.code.enums.RecordStatus;
import com.sibanarayan.code.events.ProblemEvent;
import com.sibanarayan.code.exceptions.ResourceNotFoundException;
import com.sibanarayan.code.models.request.CreateProblemRequest;
import com.sibanarayan.code.models.request.ProblemFilterRequest;
import com.sibanarayan.code.models.response.ProblemResponse;
import com.sibanarayan.code.repository.ProblemRepository;
import com.sibanarayan.code.services.ProblemService;
import com.sibanarayan.code.specifications.ProblemSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProblemServiceImpl implements ProblemService {

    private final ProblemRepository problemRepository;
    private final KafkaTemplate<String, ProblemEvent> kafkaTemplate;

    private static final String PROBLEM_TOPIC = "problem.events";

    @Override
    @Transactional
    public ProblemResponse createProblem(CreateProblemRequest request, UUID adminId) {
        Problem problem = Problem.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .difficulty(request.getDifficulty())
                .categories(request.getCategories())
                .createdBy(adminId)
                .build();

        Problem saved = problemRepository.save(problem);
//        publishEvent(saved.getId(), saved.getTitle(), ProblemEventType.PROBLEM_CREATED);
        return mapToResponse(saved);
    }

    @Override
    public Page<ProblemResponse> getProblems(ProblemFilterRequest filter) {
        Pageable pageable =PageRequest.of(
                filter.getPage(),
                filter.getSize(),
                Sort.by("createdAt").descending()
        );

        return problemRepository
                .findAll(ProblemSpecification.withFilters(filter),pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional
    public void deleteProblem(UUID problemId) {
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new ResourceNotFoundException("Problem not found"));

        problem.setRecordStatus(RecordStatus.DELETED);
        problemRepository.save(problem);
        publishEvent(problem.getId(), problem.getTitle(), ProblemEventType.PROBLEM_DELETED);
    }

    private void publishEvent(UUID problemId, String title, ProblemEventType eventType) {
        ProblemEvent event = ProblemEvent.builder()
                .problemId(problemId)
                .title(title)
                .eventType(eventType)
                .occurredAt(Instant.now())
                .build();

        kafkaTemplate.send(PROBLEM_TOPIC, problemId.toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish {} event for problem {}",
                                eventType, problemId, ex);
                    } else {
                        log.info("Published {} event for problem {}",
                                eventType, problemId);
                    }
                });
    }

    @Override
    public ProblemResponse getProblemById(UUID problemId) {
        Problem problem = problemRepository.findById(problemId)
                .filter(p -> p.getRecordStatus() == RecordStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Problem not found"));
        return mapToResponse(problem);
    }

    private ProblemResponse mapToResponse(Problem entity) {
        return ProblemResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .difficulty(entity.getDifficulty())
                .categories(entity.getCategories())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}


