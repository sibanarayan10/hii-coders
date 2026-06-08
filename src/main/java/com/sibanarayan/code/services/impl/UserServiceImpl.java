package com.sibanarayan.code.services.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sibanarayan.code.entities.QProblem;
import com.sibanarayan.code.entities.QSubmissionResultSnapshot;
import com.sibanarayan.code.entities.QUser;
import com.sibanarayan.code.entities.User;
import com.sibanarayan.code.enums.*;
import com.sibanarayan.code.events.UserEvent;
import com.sibanarayan.code.exceptions.EntityAlreadyExistException;
import com.sibanarayan.code.models.request.CreateUserRequest;
import com.sibanarayan.code.models.request.LoginRequest;
import com.sibanarayan.code.models.response.*;
import com.sibanarayan.code.repository.UserRepository;
import com.sibanarayan.code.services.SubmissionResultSnapshotService;
import com.sibanarayan.code.services.UserService;
import com.sibanarayan.code.utility.JwtUtility;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.data.domain.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService,SubmissionResultSnapshotService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtility jwtUtility;
    private final KafkaTemplate<String, UserEvent> kafkaTemplate;
    private final JPAQueryFactory queryFactory;

    private final  String TOPIC="user.event";

    public boolean createUser(CreateUserRequest request){
        userRepository
                .findByEmail(request.getEmail())
                .ifPresent(user -> {
                    throw new EntityAlreadyExistException(
                            "User with this email already exists"
                    );
                });

        String encodedPw= passwordEncoder.encode(request.getPassword());

        User user=User.builder().
                name(request.getName()).
                email(request.getEmail()).
                role(request.getRole()).
                password(encodedPw).build();

        if(request.getPhone()!=null){
            user.setPhone(request.getPhone().toString());
        }

        if(request.getRole()==null){
            user.setRole(UserRole.USER);
        }

        userRepository.save(user);
        log.info("A new user created successfully");

        publishEvent(user.getId(),user.getEmail(),user.getName(),EventType.CREATE);
        return true;
    }
    public UserResponse loginUser(LoginRequest request, HttpServletResponse response){
        String email= request.getEmail();
        Optional<User> optUser=userRepository.findByEmail(email);

        if(optUser.isEmpty()){
           throw new ResourceNotFoundException("User not found");
        }

        User user=optUser.get();

        String encodedPw=user.getPassword();
        String password= request.getPassword();

        boolean isMatch= passwordEncoder.matches(password,encodedPw);

        if(!isMatch){
            throw new RuntimeException("Wrong credentials!");
        }

        String token = jwtUtility.generateToken(email,user.getId(),user.getRole());
        ResponseCookie cookie = ResponseCookie.from("AUTH_TOKEN", token)
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(60 * 60)
                .build();


        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .recordStatus(RecordStatus.ACTIVE)
                .build();
    }

    public UserResponse getMe( String email){

        User user=getUser(email);

        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .build();
    }

    public Page<UserResponse> getUsers(String search,int page,int limit){
        Pageable pageable =PageRequest.of(
                page,
                limit,
                Sort.by("createdAt").descending()
        );

        QUser user= QUser.user;
        QSubmissionResultSnapshot submissionResultSnapshot=QSubmissionResultSnapshot.submissionResultSnapshot;

        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.hasText(search)) {
            builder.and(user.name.containsIgnoreCase(search)
                    .or(user.email.containsIgnoreCase(search)));
        }

        NumberExpression<Long> acceptedCount = new CaseBuilder()
                .when(submissionResultSnapshot.status.eq(SubmissionStatus.ACCEPTED))
                .then(submissionResultSnapshot.problemId)
                .otherwise((UUID) null)
                .countDistinct();

        List<Tuple> list=queryFactory.select(
                user,
                acceptedCount
                )
                .from(user)
                .leftJoin(submissionResultSnapshot)
                .on(submissionResultSnapshot.userId.eq(user.id))
                .where(builder)
                .groupBy(
                        user.id,
                        user.name,
                        user.email,
                        user.role,
                        user.phone,
                        user.recordStatus,
                        user.createdAt,
                        user.updatedAt,
                        user.password
                )
                .orderBy(user.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<UserResponse>content=list.stream().map(tuple-> {
            User record=tuple.get(user);
            return UserResponse.builder()
                    .recordStatus(record.getRecordStatus())
                    .name(record.getName())
                    .email(record.getEmail())
                    .role(record.getRole())
                    .id(record.getId())
                    .createdAt(record.getCreatedAt())
                    .acceptedCount(tuple.get(acceptedCount))
                    .build();
        }).toList();

        Long total = queryFactory
                .select(user.countDistinct())
                .from(user)
                .leftJoin(submissionResultSnapshot)
                .on(user.id.eq(submissionResultSnapshot.userId))
                .where(builder)
                .fetchOne();

        return new PageImpl<>(content, pageable, total);

    }

    public UserMetrics getUserMetrics(){
        QUser user=QUser.user;

        NumberExpression<Integer> activeUserCount=user.recordStatus
                        .when(RecordStatus.ACTIVE)
                                .then(1)
                                        .otherwise(0)
                                                .sum();



       Tuple response= queryFactory.select(
                activeUserCount,
                user.countDistinct()
                )
                .from(user)
               .fetchOne();

       return UserMetrics.builder()
               .deactivateUsers(response.get(user.countDistinct())-response.get(activeUserCount))
               .totalUsers(response.get(user.countDistinct()))
               .activeUsers(response.get(activeUserCount))
               .build();

    }

    public List<UserResponse> getRecentUsers(){
        QUser user=QUser.user;
        BooleanBuilder builder=new BooleanBuilder();

        List<User>list=queryFactory.selectFrom(user)
                .where(builder)
                .orderBy(user.createdAt.desc())
                .limit(10)
                .fetch();

       return list.stream().map(record->UserResponse.builder()
                .name(record.getName())
                .email(record.getEmail())
                .createdAt(record.getCreatedAt())
                .id(record.getId())
                .build()).toList();

    }

    public boolean toggleUserStatus(UUID userId,RecordStatus status){
          Optional<User> userExist=userRepository.findById(userId);
          if(userExist.isPresent()){
              User user=userExist.get();
              user.setRecordStatus(status);
              return true;
          }
          return false;
    }

    public UserStatistics  computeDashboardStats(UUID userId){
        QSubmissionResultSnapshot submissionResultSnapshot =QSubmissionResultSnapshot.submissionResultSnapshot;
        QProblem problem=QProblem.problem;

       List<Tuple> list= queryFactory.select(
                       submissionResultSnapshot.status,
                       submissionResultSnapshot.occurredAt,
                                        problem
                                    )
                                    .from(submissionResultSnapshot)
                                    .innerJoin(problem)
                                    .on(submissionResultSnapshot.problemId.eq(problem.id))
                                    .where(submissionResultSnapshot.userId.eq(userId)
                                            .and( problem.recordStatus.eq(RecordStatus.ACTIVE)
                                            )
                                    )
                                    .fetch();

        // Group by difficulty
        Map<ProblemDifficulty, Integer> byDifficulty = list.stream()
                .collect(Collectors.groupingBy(
                        tuple -> tuple.get(problem).getDifficulty(),
                        Collectors.summingInt(t -> 1)
                ));

        // Group by status
        Map<SubmissionStatus, Integer> byStatus = list.stream()
                .collect(Collectors.groupingBy(
                        tuple -> tuple.get(submissionResultSnapshot.status),
                        Collectors.summingInt(t -> 1)
                ));

        // Group by category
        Map<ProblemsCategory, Integer> byCategory = list.stream()
                .flatMap(tuple -> tuple.get(problem).getCategories().stream())
                .collect(Collectors.groupingBy(
                        category -> category,
                        Collectors.summingInt(c -> 1)
                ));

        return UserStatistics.builder()
                .submissionByDifficulty(byDifficulty)
                .submissionByStatus(byStatus)
                .submissionByCategory(byCategory)
                .totalSubmission(list.size())
                .build();

    }

    public List<SubmissionResponse> getRecentSubmission(UUID userId){
        QSubmissionResultSnapshot snapshot=QSubmissionResultSnapshot.submissionResultSnapshot;
        QProblem problem=QProblem.problem;

        List<Tuple> list=queryFactory.select(
                    snapshot.occurredAt,
                    snapshot.status,
                    problem.title ,
                    problem.difficulty
                )
                .from(snapshot)
                .innerJoin(problem)
                .on(snapshot.userId.eq(userId)
                        .and(snapshot.problemId.eq(problem.id)))
                .where(problem.recordStatus.eq(RecordStatus.ACTIVE))
                .fetch();

       return list.stream().map(tuple->SubmissionResponse.builder()
                .status(tuple.get(snapshot.status))
                .createdAt(tuple.get(snapshot.occurredAt))
                .difficulty(tuple.get(problem.difficulty))
                .problemTitle(tuple.get(problem.title))
                .build()).toList();
       
    }

    private User getUser(String email){
        Optional<User> optUser=userRepository.findByEmail(email);

        if(optUser.isEmpty()){
            throw new ResourceNotFoundException("User not found");
        }

        return optUser.get();
    }

    private void publishEvent(UUID userId,String email,String name, EventType eventType) {
        UserEvent event=UserEvent.builder()
                .id(userId)
                .email(email)
                .name(name)
                .occurredAt(Instant.now())
                .eventType(eventType)
                .build();

        kafkaTemplate.send(TOPIC, userId.toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish {} event for user {}",
                                eventType, userId, ex);
                    } else {
                        log.info("Published {} event for user {}",
                                eventType, userId);
                    }
                });
    }

}
