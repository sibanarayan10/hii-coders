package com.sibanarayan.code.controllers;


import com.sibanarayan.code.customAnnotation.Role;
import com.sibanarayan.code.models.request.CreateUserRequest;
import com.sibanarayan.code.models.request.LoginRequest;
import com.sibanarayan.code.models.response.UserResponse;
import com.sibanarayan.code.security.JwtFilter;
import com.sibanarayan.code.services.UserService;
import com.sibanarayan.code.utility.JwtUtility;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtility jwtUtility;
    private final JwtFilter jwtFilter;

    @PostMapping("user/sign-up")
    private ResponseEntity<?> register(@RequestBody CreateUserRequest userRequest){
        userService.createUser(userRequest);
        return ResponseEntity.status(201).body("User registered successfully");
    }

    @PostMapping("user/sign-in")
    public ResponseEntity<UserResponse> login(
            @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        UserResponse result=userService.loginUser(request,response);
       return new ResponseEntity<UserResponse>(result, HttpStatus.ACCEPTED);
    }
    @GetMapping("user/me")
    public ResponseEntity<UserResponse> getMe(
            HttpServletRequest request
    ) {
        String token=jwtFilter.extractTokenFromCookie(request);
        String email= jwtUtility.getEmail(token);
        UserResponse result=userService.getMe(email);
        return new ResponseEntity<UserResponse>(result, HttpStatus.ACCEPTED);
    }

    @PostMapping("user/log-out")
    public ResponseEntity<Boolean>logout(HttpServletRequest request,HttpServletResponse response){

        Cookie cookie = new Cookie("AUTH_TOKEN", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        response.addCookie(cookie);

        return ResponseEntity.ok(true);
    }

    @GetMapping("users")
    @Role("ADMIN")
    public ResponseEntity<List<UserResponse>>  getUsers( HttpServletRequest request){
        String token=jwtFilter.extractTokenFromCookie(request);
        String email= jwtUtility.getEmail(token);
        List<UserResponse> result= userService.getUsers(email);
        return new ResponseEntity<>(result,HttpStatus.ACCEPTED);
    }



}
