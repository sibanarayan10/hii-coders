package com.sibanarayan.code.interceptors;

import com.sibanarayan.code.customAnnotation.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;

@Component
@AllArgsConstructor
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


        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + requiredRole))) {
            return true;
        }

        // Reject request
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        response.getWriter().write("Access Denied");

        return false;
    }
}