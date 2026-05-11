package com.sibanarayan.code.services;

import com.sibanarayan.code.models.request.CreateUserRequest;
import com.sibanarayan.code.models.request.LoginRequest;
import com.sibanarayan.code.models.response.ProblemResponse;
import com.sibanarayan.code.models.response.UserResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

public interface UserService {
    boolean createUser(CreateUserRequest request);
    UserResponse loginUser(LoginRequest request, HttpServletResponse response);
    UserResponse getMe( String email);
    List<UserResponse> getUsers(String email);
}
