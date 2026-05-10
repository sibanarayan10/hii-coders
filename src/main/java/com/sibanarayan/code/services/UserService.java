package com.sibanarayan.code.services;

import com.sibanarayan.code.models.request.CreateUserRequest;
import com.sibanarayan.code.models.request.LoginRequest;
import com.sibanarayan.code.models.response.LoginResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface UserService {
    boolean createUser(CreateUserRequest request);
    LoginResponse loginUser(LoginRequest request, HttpServletResponse response);
    LoginResponse getMe( HttpServletRequest request);
}
