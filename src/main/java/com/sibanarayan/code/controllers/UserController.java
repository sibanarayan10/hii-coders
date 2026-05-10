package com.sibanarayan.code.controllers;


import com.sibanarayan.code.models.request.CreateUserRequest;
import com.sibanarayan.code.models.request.LoginRequest;
import com.sibanarayan.code.models.response.LoginResponse;
import com.sibanarayan.code.services.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("user/sign-up")
    private ResponseEntity<?> register(@RequestBody CreateUserRequest userRequest){
        userService.createUser(userRequest);
        return ResponseEntity.status(201).body("User registered successfully");
    }

    @PostMapping("user/sign-in")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
       LoginResponse result=userService.loginUser(request,response);
       return new ResponseEntity<LoginResponse>(result, HttpStatus.ACCEPTED);
    }
    @GetMapping("user/me")
    public ResponseEntity<LoginResponse> getMe(
            HttpServletRequest request
    ) {
        LoginResponse result=userService.getMe(request);
        return new ResponseEntity<LoginResponse>(result, HttpStatus.ACCEPTED);
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

}
