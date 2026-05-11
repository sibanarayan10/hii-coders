package com.example.security;

import com.sibanarayan.code.customAnnotation.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;

@Component
public class RoleInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) throws Exception {

        // Ignore non-controller requests
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        Method method = handlerMethod.getMethod();

        Role requireRole =
                method.getAnnotation(Role.class);

        if (requireRole == null) {
            return true;
        }

        String requiredRole = requireRole.value();

        String userRole = request.getHeader("ROLE");

        // Check role
        if (requiredRole.equals(userRole)) {
            return true;
        }

        // Reject request
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        response.getWriter().write("Access Denied");

        return false;
    }
}