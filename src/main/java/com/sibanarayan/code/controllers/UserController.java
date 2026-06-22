package com.sibanarayan.code.controllers;


import com.sibanarayan.code.customAnnotation.Role;
import com.sibanarayan.code.entities.PendingLink;
import com.sibanarayan.code.entities.User;
import com.sibanarayan.code.enums.RecordStatus;
import com.sibanarayan.code.enums.UserDetailProvider;
import com.sibanarayan.code.models.request.CreateUserRequest;
import com.sibanarayan.code.models.request.LoginRequest;
import com.sibanarayan.code.models.response.*;
import com.sibanarayan.code.config.JwtFilter;
import com.sibanarayan.code.repository.PendingLinkRepository;
import com.sibanarayan.code.repository.UserRepository;
import com.sibanarayan.code.services.SubmissionResultSnapshotService;
import com.sibanarayan.code.services.UserService;
import com.sibanarayan.code.services.impl.EmailService;
import com.sibanarayan.code.utility.JwtUtility;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtility jwtUtility;
    private final JwtFilter jwtFilter;
    private final SubmissionResultSnapshotService submissionResultSnapshotService;
    private final PendingLinkRepository pendingLinkRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @PostMapping("/api/v1/users/sign-up")
    private ResponseEntity<?> register(@RequestBody CreateUserRequest userRequest){
        userService.createUser(userRequest);
        return ResponseEntity.status(201).body("User registered successfully");
    }

    @PostMapping("/api/v1/users/sign-in")
    public ResponseEntity<UserResponse> login(
            @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        UserResponse result=userService.loginUser(request,response);
       return new ResponseEntity<UserResponse>(result, HttpStatus.ACCEPTED);
    }
    @GetMapping("/api/v1/users/me")
    public ResponseEntity<UserResponse> getMe(
            HttpServletRequest request
    ) {
        String token=jwtFilter.extractTokenFromCookie(request);
        String email= jwtUtility.getEmail(token);
        UserResponse result=userService.getMe(email);
        return new ResponseEntity<UserResponse>(result, HttpStatus.ACCEPTED);
    }

    @PostMapping("/api/v1/users/log-out")
    public ResponseEntity<Boolean>logout(HttpServletResponse response){

        Cookie cookie = new Cookie("AUTH_TOKEN", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        response.addCookie(cookie);

        return ResponseEntity.ok(true);
    }

    @GetMapping("/api/v1/users/{userId}/dashboard")
    public ResponseEntity<UserStatistics> computeDashboardStats(@PathVariable UUID userId){
        return new ResponseEntity<>(submissionResultSnapshotService.computeDashboardStats(userId),HttpStatus.ACCEPTED);
    }
    @GetMapping("/api/v1/user/auth/link-account")
    public ResponseEntity<?> linkAccount(@RequestParam String token) {

        Optional<PendingLink> optional = pendingLinkRepository.findByToken(token);

        if (optional.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid link.");
        }
        PendingLink pendingLink=optional.get();

        if (pendingLink.getExpiresAt().isBefore(LocalDateTime.now())) {
            pendingLinkRepository.delete(pendingLink);
            return ResponseEntity.badRequest().body("Link expired. Please try again.");
        }

        // Update user record
        Optional<User> user = userRepository.findByEmail(pendingLink.getEmail());
        user.ifPresent((u)->{
            u.setPassword(pendingLink.getPassword());
            u.getUserDetailProvider().add(UserDetailProvider.MANUAL);
            userRepository.save(u);
        });

        pendingLinkRepository.delete(pendingLink);

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("http://localhost:5173/sign-in"))
                .build();
    }
    @PostMapping("/api/v1/users/auth/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> body, @RequestParam String token){
        String password=body.get("password");
        return userService.resetPassword(password,token);
    }

    @GetMapping("/api/v1/users/auth/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email not registered"));

        String token = UUID.randomUUID().toString();

        PendingLink pendingLink = new PendingLink();
        pendingLink.setEmail(email);
        pendingLink.setToken(token);
        pendingLink.setPassword("abc");
        pendingLink.setExpiresAt(LocalDateTime.now().plusHours(1));
        pendingLinkRepository.save(pendingLink);

        emailService.sendPasswordResetEmail(email, user.getName(), token);

        return ResponseEntity.ok("Check your email to reset your password.");
    }

    @GetMapping("/api/v1/users/{userId}/submissions/recent")
    public ResponseEntity<List<SubmissionResponse>> getRecentSubmission(@PathVariable UUID userId){
        return new ResponseEntity<>(submissionResultSnapshotService.getRecentSubmission(userId),HttpStatus.ACCEPTED);
    }
    @GetMapping("/api/v1/users")
    @Role("ADMIN")
    public ResponseEntity<Page<UserResponse>>  getUsers(@RequestParam String search,@RequestParam int page,@RequestParam int limit ){
        Page<UserResponse> result= userService.getUsers(search,page,limit);
        return new ResponseEntity<>(result,HttpStatus.ACCEPTED);
    }

    @PutMapping("/api/v1/users/{userId}")
    @Role("ADMIN")
    public ResponseEntity<Boolean> toggleUserStatus(@PathVariable UUID userId, @RequestParam RecordStatus status) {
        boolean result=userService.toggleUserStatus(userId,status);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/api/v1/users/recent")
    @Role("ADMIN")
    public ResponseEntity<List<UserResponse>> getRecentUsers(){
        return new ResponseEntity<>(userService.getRecentUsers(),HttpStatus.ACCEPTED);
    }

    @GetMapping("/api/v1/users/metrics")
    @Role("ADMIN")
    public ResponseEntity<UserMetrics> getUserMetrics(){
        return new ResponseEntity<>(userService.getUserMetrics(),HttpStatus.ACCEPTED);
    }


}
