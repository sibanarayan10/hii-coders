package com.sibanarayan.code.config;

import com.sibanarayan.code.utility.CustomOAuthUser;
import com.sibanarayan.code.utility.JwtUtility;
import com.sibanarayan.shared_package.enums.UserRole;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
@AllArgsConstructor
public class OAuth2SuccessHandler
        implements AuthenticationSuccessHandler {

    private final JwtUtility jwtUtility;
    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException {
//getting the user email and id,and making token out of it
//attaching it with the response and sending it to the backend
//
        CustomOAuthUser oauth2User =
                (CustomOAuthUser) authentication.getPrincipal();

        String email=oauth2User.getName();
        UUID userId=oauth2User.getUser().getId();
        UserRole role=oauth2User.getUser().getRole();

        String token= jwtUtility.generateToken(email,userId,role);
        ResponseCookie cookie = ResponseCookie.from("AUTH_TOKEN", token)
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(60 * 60)
                .build();


        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        response.sendRedirect(
                "http://localhost:5173"
        );
    }
}