package com.sibanarayan.code.services.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sibanarayan.code.entities.*;
import com.sibanarayan.code.enums.*;
import com.sibanarayan.code.events.ProblemEvent;
import com.sibanarayan.code.exceptions.EntityAlreadyExistException;
import com.sibanarayan.code.exceptions.ResourceNotFoundException;
import com.sibanarayan.code.models.request.AdminProblemPageFilter;
import com.sibanarayan.code.models.request.CreateProblemRequest;
import com.sibanarayan.code.models.request.ProblemFilterRequest;
import com.sibanarayan.code.models.request.TestCaseRequest;
import com.sibanarayan.code.models.response.AdminProblemResponse;
import com.sibanarayan.code.models.response.ProblemResponse;
import com.sibanarayan.code.models.response.ProblemUserEngagementResponse;
import com.sibanarayan.code.models.response.TestCaseResponse;
import com.sibanarayan.code.repository.ProblemRepository;
import com.sibanarayan.code.repository.TestCaseRepository;
import com.sibanarayan.code.services.ProblemService;
import com.sibanarayan.code.specifications.ProblemSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProblemServiceImpl implements ProblemService {

    private final ProblemRepository problemRepository;
    private final KafkaTemplate<String, ProblemEvent> kafkaTemplate;
    private final TestCaseRepository testCaseRepository;
    private final  JPAQueryFactory queryFactory;

    private static final String PROBLEM_TOPIC = "problem.events";

    @Override
    @Transactional
    public ProblemResponse createProblem(CreateProblemRequest request, UUID adminId) {
        Problem problem = Problem.builder()
                .title(request.getTitle())
                .blocks(request.getBlocks())
                .difficulty(request.getDifficulty())
                .categories(request.getCategories())
                .createdBy(adminId)
                .solutionsByLanguage(request.getSolutionByLanguage())
                .ioByLanguage(request.getIoByLanguage())
                .build();

        boolean isNew=request.getId()==null;

        if(!isNew) problem.setId(request.getId());

        Problem saved = problemRepository.save(problem);

        log.info("New problem created with id {}",saved.getId());

        if(isNew)
            publishEvent(saved.getId(), saved.getTitle(), EventType.CREATE,request.getIoByLanguage());

        return mapToResponse(saved);
    }

    @Override
    public Page<ProblemResponse> getProblems(ProblemFilterRequest filter, UUID userId) {
        Pageable pageable =PageRequest.of(
                filter.getPage()-1,
                filter.getSize(),
                Sort.by("createdAt").descending()
        );

        QProblem problem = QProblem.problem;

        QUserProblemEngagement engagement =
                QUserProblemEngagement.userProblemEngagement;

        BooleanBuilder builder =
                new BooleanBuilder();

        builder.and(
                problem.recordStatus.eq(
                        RecordStatus.ACTIVE
                )
        );

        if (filter.getCategories() != null &&
                !filter.getCategories().isEmpty()) {

            builder.and(
                    problem.categories.any().in(
                            filter.getCategories()
                                    .toArray(
                                            new ProblemsCategory[0]
                                    )
                    )
            );
        }

        if (filter.getCompanies() != null &&
                !filter.getCompanies().isEmpty()) {

            builder.and(
                    problem.companies.any().in(
                            filter.getCompanies()
                                    .toArray(
                                            new Company[0]
                                    )
                    )
            );
        }

        if (filter.getDifficulties() != null &&
                !filter.getDifficulties().isEmpty()) {

            builder.and(
                    problem.difficulty.in(
                            filter.getDifficulties()
                    )
            );
        }

        if (filter.getStatus() != null && filter.getStatus()!=SolveStatus.TODO) {
            builder.and(
                    engagement.solveStatus.eq(
                            filter.getStatus()
                    )
            );
        }


        List<Problem> problems = queryFactory
                                    .selectFrom(problem)
                                    .leftJoin(engagement)
                                    .on(
                                        engagement.problem.id.eq(problem.id),
                                        engagement.userId.eq(userId)
                                    )
                                    .where(builder)
                                    .offset(pageable.getOffset())
                                    .limit(pageable.getPageSize())
                                    .fetch();


        List<UUID> problemIds = problems.stream()
                .map(Problem::getId)
                .toList();

        Map<UUID, SolveStatus> statusMap = queryFactory
                .selectFrom(engagement)
                .where(
                        engagement.problem.id.in(problemIds),
                        engagement.userId.eq(userId)
                )
                .fetch()
                .stream()
                .collect(Collectors.toMap(
                        e -> e.getProblem().getId(),
                        e -> e.getSolveStatus()
                ));

        Long total = queryFactory
                        .select(
                                problem.countDistinct()
                        )
                        .from(problem)
                        .leftJoin(engagement)
                        .on(
                                engagement.problem.id.eq(problem.id),
                                engagement.userId.eq(userId)
                        )
                        .where(builder)
                        .fetchOne();

        List<ProblemResponse> content = problems.stream()
                .map(p -> ProblemResponse.builder()
                        .id(p.getId())
                        .title(p.getTitle())
                        .difficulty(p.getDifficulty())
                        .blocks(p.getBlocks())
                        .categories(p.getCategories())
                        .status(statusMap.getOrDefault(p.getId(), SolveStatus.TODO))
                        .solutionByLanguage(p.getSolutionsByLanguage())
                        .build()
                )
                .toList();
        return new PageImpl<>(
                content,
                pageable,
                total
        );
    }

    public Page<AdminProblemResponse> getSystemProblems(AdminProblemPageFilter filter) {
        Pageable pageable =PageRequest.of(
                filter.getPage(),
                filter.getSize(),
                Sort.by("createdAt").descending()
        );
        QProblem problem=QProblem.problem;
        QSubmissionResultSnapshot submissionResultSnapshot=QSubmissionResultSnapshot.submissionResultSnapshot;
        BooleanBuilder builder=new BooleanBuilder();

        builder.and(problem.recordStatus.eq(RecordStatus.ACTIVE));

        if(!filter.getSearch().isEmpty()){
            builder.and(problem.title.containsIgnoreCase(filter .getSearch()));
        }
        if(filter.getDifficulty()!=null){
            builder.and(problem.difficulty.eq(filter.getDifficulty()));
        }


        OrderSpecifier<?> order;

        if (filter.getOrder().equals("asc")) {
            order = filter.getSortBy().equals("createdAt") ? problem.createdAt.asc() : problem.difficulty.asc();
        } else {
            order = filter.getSortBy().equals("createdAt") ? problem.createdAt.desc() : problem.difficulty.desc();
        }

        NumberExpression<Integer> acceptedCount = submissionResultSnapshot.status
                .when(SubmissionStatus.ACCEPTED)
                .then(1)
                .otherwise(0)
                .sum();


        List<Tuple> list = queryFactory
                .select(problem, submissionResultSnapshot.countDistinct(), acceptedCount)
                .from(problem)
                .leftJoin(submissionResultSnapshot).on(problem.id.eq(submissionResultSnapshot.problemId))
                .groupBy(problem.id)
                .where(builder)
                .orderBy(order)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(problem.countDistinct())
                .from(problem)
                .leftJoin(submissionResultSnapshot)
                .on(problem.id.eq(submissionResultSnapshot.problemId))
                .where(builder)
                .fetchOne();

        List<AdminProblemResponse> content = list.stream().map(tuple -> {
            Problem p = tuple.get(problem);  // ← get the whole entity first
            Long totalCount = tuple.get(submissionResultSnapshot.countDistinct());
            Integer accepted = tuple.get(acceptedCount);

            double acceptanceRate = (totalCount != null && totalCount > 0)
                    ? (accepted.doubleValue() / totalCount.doubleValue()) * 100
                    : 0.0;

            return AdminProblemResponse.builder()
                    .acceptanceRate(acceptanceRate)
                    .title(p.getTitle())           // ← from entity
                    .description(p.getBlocks())    // ← from entity
                    .id(p.getId())                 // ← from entity
                    .difficulty(p.getDifficulty()) // ← from entity
                    .categories(p.getCategories()) // ← from entity
                    .build();
        }).toList();

        return new PageImpl<>(content, pageable, total);

    }

    @Override
    @Transactional
    public void deleteProblem(UUID problemId) {
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new ResourceNotFoundException("Problem not found"));

        problem.setRecordStatus(RecordStatus.DELETED);
        problemRepository.save(problem);
        publishEvent(problem.getId(), problem.getTitle(), EventType.DELETE);
    }

    @Override
    public ProblemUserEngagementResponse getProblemUserEngagementDetail(UUID problemId, UUID userId) {
        QProblem pr =QProblem.problem;
        QUserProblemEngagement upe=QUserProblemEngagement.userProblemEngagement;


        Tuple row=queryFactory.select(
                pr,
                upe.liked,
                upe.saved,
                upe, upe.favorite
        )
                .from(pr)
                .leftJoin(upe)
                .on(pr.id.eq(upe.problem.id)
                        .and(upe.userId.eq(userId)))
                .where(pr.recordStatus.eq(RecordStatus.ACTIVE)
                        .and(pr.id.eq(problemId)))
                .fetchOne();
        if (row == null) throw new ResourceNotFoundException("Problem not found");

        Problem problem = row.get(pr);

        return new ProblemUserEngagementResponse(
                problem.getTitle(),
                problem.getBlocks(),
                Boolean.TRUE.equals(row.get(upe.saved)),
                Boolean.TRUE.equals(row.get(upe.liked)),
                Boolean.TRUE.equals(row.get(upe.favorite)),
                problem.getId(),
                problem.getDifficulty(),
                problem.getCategories()
        );
    }

    @Override
    public ProblemResponse getProblem (UUID problemId){
       Optional<Problem> exist= problemRepository.findByIdAndRecordStatus(problemId,RecordStatus.ACTIVE);
       if(exist.isEmpty()){
           throw new ResourceNotFoundException("Problem with  this id does not exist");
       }

        Problem pr=exist.get();
        return ProblemResponse.builder()
                .title(pr.getTitle())
                .blocks(pr.getBlocks())
                .id(pr.getId())
                .categories(pr.getCategories())
                .solutionByLanguage(pr.getSolutionsByLanguage())
                .difficulty(pr.getDifficulty())
                .ioByLanguage(pr.getIoByLanguage())
                .build();

    }
    public List<TestCaseResponse> getTestCasesByProblemId(UUID problemId) {
        return getTestCaseByProblemId(problemId,true);
    }

    public List<TestCaseResponse> getTestCaseByProblemId(UUID problemId,boolean isSampleOnly){
        return    testCaseRepository.getByProblemIdAndSampleAndRecordStatus(problemId,isSampleOnly,RecordStatus.ACTIVE)
                    .stream()
                    .map(this::mapToResponse)
                    .toList();
    }

    public List<TestCaseResponse> getAllByProblemId(UUID problemId){
        return  testCaseRepository.getByProblemIdAndRecordStatus(problemId,RecordStatus.ACTIVE)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public Boolean createTestCase(TestCaseRequest request) {
        var problem = problemRepository.findById(request.getProblemId())
                .orElseThrow(() -> new ResourceNotFoundException("Problem not found"));

        TestCase testCase = TestCase.builder()
                .problem(problem)
                .sample(request.isSample())
                .expectedOutput(request.getExpectedOutput())
                .inputData(request.getInputData())
                .outputFileKey(request.getOutputFileKey())
                .inputFileKey(request.getInputFileKey())
                .storageType(request.getStorageType())
                .sequenceOrder(request.getSequenceOrder())
                .memoryLimit(request.getMemoryLimit())
                .timeLimit(request.getTimeLimit())
                .build();

        testCaseRepository.save(testCase);

        return true;
    }

    private void publishEvent(UUID problemId,String title,EventType eventType){
        publishEvent(problemId,title,eventType,new HashMap<>());
    }
    private void publishEvent(UUID problemId, String title, EventType eventType,Map<ProgrammingLanguage,String> ioByLanguage) {
        ProblemEvent event = ProblemEvent.builder()
                .problemId(problemId)
                .title(title)
                .eventType(eventType)
                .ioByLanguage(ioByLanguage)
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
    private ProblemResponse mapToResponse(Problem entity) {

        return ProblemResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .difficulty(entity.getDifficulty())
                .categories(entity.getCategories())
                .build();
    }
    private TestCaseResponse mapToResponse(TestCase testCase){
        return TestCaseResponse.builder()
                .id(testCase.getId())
                .inputData(testCase.getInputData())
                .expectedOutput(testCase.getExpectedOutput())
                .sequenceOrder(testCase.getSequenceOrder())
                .build();
    }

    private <T> Set<T> safeSet(Set<T> values) {
        return values == null ? Set.of() : values;
    }

}


