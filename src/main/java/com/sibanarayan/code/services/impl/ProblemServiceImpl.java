package com.sibanarayan.code.services.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sibanarayan.code.entities.*;
import com.sibanarayan.code.enums.*;

import com.sibanarayan.code.enums.ProblemDifficulty;
import com.sibanarayan.code.enums.ProblemsCategory;
import com.sibanarayan.code.models.request.CreateProblemRequest;
import com.sibanarayan.code.models.request.ProblemFilterRequest;
import com.sibanarayan.code.models.request.TestCaseRequest;
import com.sibanarayan.code.models.response.BaseProblemResponse;
import com.sibanarayan.code.models.response.ProblemResponse;
import com.sibanarayan.code.models.response.ProblemUserEngagementResponse;
import com.sibanarayan.code.models.response.TestCaseResponse;
import com.sibanarayan.code.repository.ProblemRepository;
import com.sibanarayan.code.repository.SubmissionResultSnapshotRepository;
import com.sibanarayan.code.repository.TestCaseRepository;
import com.sibanarayan.code.repository.UserProblemEngagementRepository;
import com.sibanarayan.code.services.ProblemService;
import com.sibanarayan.shared_package.enums.*;
import com.sibanarayan.shared_package.enums.SubmissionStatus;
import com.sibanarayan.shared_package.events.ProblemEvent;
import com.sibanarayan.shared_package.exceptions.ResourceNotFoundException;
import com.sibanarayan.shared_package.security.JwtUtility;
import jakarta.servlet.http.HttpServletRequest;
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
    private final JwtUtility jwtUtility;
    private final SubmissionResultSnapshotRepository submissionResultSnapshotRepository;
    private final UserProblemEngagementRepository userProblemEngagementRepository;

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

    public Map<ProblemDifficulty,Integer> getProblemsCountByDifficulty(){
        Map<ProblemDifficulty,Integer> countByDifficulty=new HashMap<>();
        List<Problem> problems=problemRepository.findAllByRecordStatus(RecordStatus.ACTIVE);

        if(problems!=null && !problems.isEmpty() ){
            problems.forEach(p->{
                Integer value=countByDifficulty.getOrDefault(p.getDifficulty(),0);
                countByDifficulty.put(p.getDifficulty(),value+1);
            });
        }

        return countByDifficulty;
    }
    @Override
    public Page<BaseProblemResponse> getProblems(ProblemFilterRequest filter, HttpServletRequest request) {
        String token=getToken(request);
        UUID userId=( token==null || token.isBlank()) ?null: getUserId(token);

        Pageable pageable =PageRequest.of(
                filter.getPage()-1,
                filter.getSize(),
                Sort.by("createdAt").descending()
        );

        if(userId == null){
            return getProblemsForAdmin(false,filter.getSearch(),filter.getDifficulties(),filter.getCategories(),filter.getCompanies(),filter.getSortBy(),filter.getOrder(),pageable);
        }

        return getProblemsForUser(filter.getSearch(),filter.getDifficulties(),filter.getCategories(),filter.getCompanies(),filter.getStatus(),filter.getSortBy(),filter.getOrder(),userId,pageable);
    }

    private Page<BaseProblemResponse> getProblemsForUser(String search,Set<ProblemDifficulty> difficulties,Set<ProblemsCategory> categories,Set<Company> companies,SolveStatus status,String sortBy,String sortOrder,UUID userId,Pageable pageable){
        QProblem problem = QProblem.problem;
        QUserProblemEngagement engagement = QUserProblemEngagement.userProblemEngagement;
        BooleanBuilder builder = new BooleanBuilder();


        builder.and( problem.recordStatus.eq( RecordStatus.ACTIVE ) );

        if (categories != null &&  !categories.isEmpty()) {
            builder.and( problem.categories.any().in(categories.toArray( new ProblemsCategory[0] ) ));
        }
        if (companies != null && !companies.isEmpty()) {
            builder.and(  problem.companies.any().in(companies.toArray(new Company[0])));
        }
        if(!search.isEmpty()){
            builder.and(problem.title.containsIgnoreCase(search));
        }
        if (difficulties != null && !difficulties.isEmpty()) {
            builder.and(problem.difficulty.in( difficulties ));
        }

        if (status != null && status!=SolveStatus.TODO) {
            builder.and( engagement.solveStatus.eq(status));
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

        QSubmissionResultSnapshot snapshot = QSubmissionResultSnapshot.submissionResultSnapshot;

        NumberExpression<Integer> acceptedCount = snapshot.status
                .when(SubmissionStatus.ACCEPTED)
                .then(1)
                .otherwise(0)
                .sum();

        Map<UUID, Double> acceptanceRateMap = queryFactory
                .select(snapshot.problemId,
                        snapshot.count(),
                        acceptedCount)
                .from(snapshot)
                .where(snapshot.problemId.in(problemIds))
                .groupBy(snapshot.problemId)
                .fetch()
                .stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(snapshot.problemId),
                        tuple -> {
                            Long totalCount = tuple.get(snapshot.count());
                            Integer accepted = tuple.get(acceptedCount);

                            return (totalCount != null && totalCount > 0)
                                    ? (accepted.doubleValue() / totalCount.doubleValue()) * 100
                                    : 0.0;
                        }
                ));
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

        List<BaseProblemResponse> content = problems.stream()
                .map(p -> {
                    ProblemResponse response = new ProblemResponse();

                    response.setId(p.getId());
                    response.setTitle(p.getTitle());
                    response.setDifficulty(p.getDifficulty());
                    response.setDescription(p.getBlocks());
                    response.setCategories(p.getCategories());
                    response.setStatus(statusMap.getOrDefault(p.getId(), SolveStatus.TODO));
                    response.setSolutionByLanguage(p.getSolutionsByLanguage());
                    response.setAcceptanceRate(
                            acceptanceRateMap.getOrDefault(p.getId(), 0.0)
                    );
                    return (BaseProblemResponse)response;
                })
                .toList();
        return new PageImpl<>(
                content,
                pageable,
                total
        );
    }
    public Page<BaseProblemResponse> getProblemsForAdmin(ProblemFilterRequest filter,HttpServletRequest request){

        Pageable pageable =PageRequest.of(
                filter.getPage(),
                filter.getSize(),
                Sort.by("createdAt").descending()
        );
        return getProblemsForAdmin(true,filter.getSearch(),filter.getDifficulties(),filter.getCategories(),filter.getCompanies(),filter.getSortBy(),filter.getOrder(),pageable);
    }
    private Page<BaseProblemResponse> getProblemsForAdmin(boolean showAllProblems, String search, Set<ProblemDifficulty> difficulties, Set<ProblemsCategory> categories,Set<Company> companies, String sortBy, String sortOrder, Pageable pageable) {

        QProblem problem=QProblem.problem;
        QSubmissionResultSnapshot submissionResultSnapshot=QSubmissionResultSnapshot.submissionResultSnapshot;
        BooleanBuilder builder=new BooleanBuilder();

        if(!showAllProblems)
            builder.and(problem.recordStatus.eq(RecordStatus.ACTIVE));

        if (categories != null &&  !categories.isEmpty()) {
            builder.and( problem.categories.any().in(categories.toArray( new ProblemsCategory[0] ) ));
        }

        if (companies != null && !companies.isEmpty()) {
            builder.and(  problem.companies.any().in(companies.toArray(new Company[0])));
        }

        if(search!=null && !search.isEmpty()){
            builder.and(problem.title.containsIgnoreCase(search));
        }
        if(difficulties!=null && !difficulties.isEmpty()){
            builder.and(problem.difficulty.in(difficulties));
        }


        OrderSpecifier<?> order=null;

        if(sortOrder!=null){
            if (sortOrder.equals("asc")) {
                order = sortBy.equals("createdAt") ? problem.createdAt.asc() : problem.difficulty.asc();
            } else {
                order = sortBy.equals("createdAt") ? problem.createdAt.desc() : problem.difficulty.desc();
            }
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
                .orderBy(order==null?problem.order.asc() : order)
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

        List<BaseProblemResponse> content = list.stream().map(tuple -> {
            Problem p = tuple.get(problem);
            Long totalCount = tuple.get(submissionResultSnapshot.countDistinct());
            Integer accepted = tuple.get(acceptedCount);

            double acceptanceRate = (totalCount != null && totalCount > 0)
                    ? (accepted.doubleValue() / totalCount.doubleValue()) * 100
                    : 0.0;

            BaseProblemResponse response = new BaseProblemResponse();

            response.setAcceptanceRate(acceptanceRate);
            response.setTitle(p.getTitle());
            response.setDescription(p.getBlocks());
            response.setId(p.getId());
            response.setDifficulty(p.getDifficulty());
            response.setCategories(p.getCategories());

            return response;
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

        BooleanBuilder joinCondition=new BooleanBuilder();
        joinCondition.and(pr.id.eq(upe.problem.id));

        double acceptanceRate=0.0;
        SolveStatus status=SolveStatus.TODO;

        List<SubmissionResultSnapshot> submissions=submissionResultSnapshotRepository.findByProblemId(problemId);
        if(submissions!=null && !submissions.isEmpty()){
            double totalSubmissions=submissions.size();
            List<SubmissionResultSnapshot> accepted=submissions.stream().filter(s->s.getStatus()==SubmissionStatus.ACCEPTED).toList();
            double acceptedCount=accepted.size();
            if(userId!=null) {
                List<SubmissionResultSnapshot> userSubmissions = submissions.stream().filter(s -> s.getUserId().equals(userId)).toList();
                if(!userSubmissions.isEmpty()){
                    status=SolveStatus.ATTEMPTED;
                }
                boolean foundAcceptedSubmission=!userSubmissions.stream().filter(s->s.getStatus()==SubmissionStatus.ACCEPTED).toList().isEmpty();
                if(foundAcceptedSubmission){
                    status=SolveStatus.SOLVED;
                }
            }
            acceptanceRate=(acceptedCount/totalSubmissions)*100;
        }


        if (userId != null) {
            joinCondition.and(upe.userId.eq(userId));
        }

        Tuple row=queryFactory.select(
                pr,
                upe.liked,
                upe.saved,
                upe.favorite
                )
                .from(pr)
                .leftJoin(upe)
                .on(joinCondition)
                .where(pr.recordStatus.eq(RecordStatus.ACTIVE)
                        .and(pr.id.eq(problemId)))
                .fetchFirst();

        if (row == null) throw new ResourceNotFoundException("Problem not found");

        Problem problem = row.get(pr);

        return  new ProblemUserEngagementResponse(
                problem.getTitle(),
                problem.getBlocks(),
                Boolean.TRUE.equals(row.get(upe.saved)),
                Boolean.TRUE.equals(row.get(upe.liked)),
                Boolean.TRUE.equals(row.get(upe.favorite)),
                problem.getTotalLikes(),
                problem.getTotalDislikes(),
                acceptanceRate,
                status,
                problem.getId(),
                problem.getDifficulty(),
                problem.getCategories()
        );

    }

    @Override
    public ProblemResponse getProblem(UUID problemId){
       Optional<Problem> exist= problemRepository.findByIdAndRecordStatus(problemId,RecordStatus.ACTIVE);
       if(exist.isEmpty()){
           throw new ResourceNotFoundException("Problem with  this id does not exist");
       }

        Problem pr=exist.get();
        return ProblemResponse.builder()
                .title(pr.getTitle())
                .description(pr.getBlocks())
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

    public boolean toggleLike(UUID problemId,UUID userId){
        Optional<UserProblemEngagement> upeWrapper= userProblemEngagementRepository.findByUserIdAndProblem_Id(userId,problemId);

        UserProblemEngagement upe;

        Optional<Problem> problemWrapper=problemRepository.findByIdAndRecordStatus(problemId,RecordStatus.ACTIVE);
        if(problemWrapper.isEmpty()){
            throw  new ResourceNotFoundException("Invalid request");
        }

        Problem p=problemWrapper.get();

        if(upeWrapper.isPresent()){
             upe=upeWrapper.get();
             boolean prevValue=upe.isLiked();
             upe.setLiked(!prevValue);
        }else{
            upe= UserProblemEngagement.builder()
                    .liked(true)
                    .saved(false)
                    .favorite(false)
                    .problem(p)
                    .solveStatus(SolveStatus.TODO)
                    .userId(userId)
                    .build();
        }

        userProblemEngagementRepository.save(upe);
        log.info("User {} response on the problem {} updated successfully",userId,problemId);

        int totalLikes=p.getTotalLikes()==null?0:p.getTotalLikes();
        p.setTotalLikes(upe.isLiked()?totalLikes+1:Math.max(0,totalLikes-1));

        problemRepository.save(p);

        log.info("Problem {} totalLike count updated successfully",problemId);

        return true;
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



    private String getToken(HttpServletRequest request){
        return jwtUtility.extractTokenFromCookie(request);
    }

    private UUID getUserId(String token){
        return jwtUtility.getUserId(token);
    }

    private String getUserRole(String token){
        return  jwtUtility.getCurrentUser().getRole();
    }

}


