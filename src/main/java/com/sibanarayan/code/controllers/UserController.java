package com.sibanarayan.code.controllers;


import com.sibanarayan.code.customAnnotation.Role;
import com.sibanarayan.code.enums.RecordStatus;
import com.sibanarayan.code.models.request.CreateUserRequest;
import com.sibanarayan.code.models.request.LoginRequest;
import com.sibanarayan.code.models.response.*;
import com.sibanarayan.code.config.JwtFilter;
import com.sibanarayan.code.services.SubmissionResultSnapshotService;
import com.sibanarayan.code.services.UserService;
import com.sibanarayan.code.utility.JwtUtility;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtility jwtUtility;
    private final JwtFilter jwtFilter;
    private final SubmissionResultSnapshotService submissionResultSnapshotService;

    @PostMapping("sign-up")
    private ResponseEntity<?> register(@RequestBody CreateUserRequest userRequest){
        userService.createUser(userRequest);
        return ResponseEntity.status(201).body("User registered successfully");
    }

    @PostMapping("sign-in")
    public ResponseEntity<UserResponse> login(
            @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        UserResponse result=userService.loginUser(request,response);
       return new ResponseEntity<UserResponse>(result, HttpStatus.ACCEPTED);
    }
    @GetMapping("me")
    public ResponseEntity<UserResponse> getMe(
            HttpServletRequest request
    ) {
        String token=jwtFilter.extractTokenFromCookie(request);
        String email= jwtUtility.getEmail(token);
        UserResponse result=userService.getMe(email);
        return new ResponseEntity<UserResponse>(result, HttpStatus.ACCEPTED);
    }

    @PostMapping("log-out")
    public ResponseEntity<Boolean>logout(HttpServletResponse response){

        Cookie cookie = new Cookie("AUTH_TOKEN", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        response.addCookie(cookie);

        return ResponseEntity.ok(true);
    }

    @GetMapping("{userId}/dashboard")
    public ResponseEntity<UserStatistics> computeDashboardStats(@PathVariable UUID userId){
        return new ResponseEntity<>(submissionResultSnapshotService.computeDashboardStats(userId),HttpStatus.ACCEPTED);
    }

    @GetMapping("{userId}/submissions/recent")
    public ResponseEntity<List<SubmissionResponse>> getRecentSubmission(@PathVariable UUID userId){
        return new ResponseEntity<>(submissionResultSnapshotService.getRecentSubmission(userId),HttpStatus.ACCEPTED);
    }
    @GetMapping()
    @Role("ADMIN")
    public ResponseEntity<Page<UserResponse>>  getUsers(@RequestParam String search,@RequestParam int page,@RequestParam int limit ){
        Page<UserResponse> result= userService.getUsers(search,page,limit);
        return new ResponseEntity<>(result,HttpStatus.ACCEPTED);
    }

    @PutMapping("/{userId}")
    @Role("ADMIN")
    public ResponseEntity<Boolean> toggleUserStatus(@PathVariable UUID userId, @RequestParam RecordStatus status) {
        boolean result=userService.toggleUserStatus(userId,status);
        return ResponseEntity.ok(result);
    }

    @GetMapping("recent")
    @Role("ADMIN")
    public ResponseEntity<List<UserResponse>> getRecentUsers(){
        return new ResponseEntity<>(userService.getRecentUsers(),HttpStatus.ACCEPTED);
    }

    @GetMapping("metrics")
    @Role("ADMIN")
    public ResponseEntity<UserMetrics> getUserMetrics(){
        return new ResponseEntity<>(userService.getUserMetrics(),HttpStatus.ACCEPTED);
    }


}
