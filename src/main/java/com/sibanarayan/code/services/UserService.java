package com.sibanarayan.code.services;

import com.sibanarayan.code.enums.RecordStatus;
import com.sibanarayan.code.models.request.CreateUserRequest;
import com.sibanarayan.code.models.request.LoginRequest;
import com.sibanarayan.code.models.response.UserMetrics;
import com.sibanarayan.code.models.response.UserResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface UserService {
    boolean createUser(CreateUserRequest request);
    UserResponse loginUser(LoginRequest request, HttpServletResponse response);
    UserResponse getMe( String email);
    Page<UserResponse> getUsers(String search, int page, int limit);
    boolean toggleUserStatus(UUID userId, RecordStatus status);
    List<UserResponse> getRecentUsers();
    UserMetrics getUserMetrics();
}
