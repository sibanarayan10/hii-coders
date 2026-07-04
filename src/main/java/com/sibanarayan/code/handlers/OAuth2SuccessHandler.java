package com.sibanarayan.code.handlers;

import com.sibanarayan.code.utility.CookieUtility;
import com.sibanarayan.code.utility.CustomOAuthUser;
import com.sibanarayan.code.utility.JwtUtility;
import com.sibanarayan.shared_package.enums.UserRole;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
public class OAuth2SuccessHandler
        implements AuthenticationSuccessHandler {

    private final JwtUtility jwtUtility;
    private final CookieUtility cookieUtility;
    private final String frontendUrl;

    public OAuth2SuccessHandler(
            JwtUtility jwtUtility,
            CookieUtility cookieUtility,
            @Value("${app.frontend.url}") String frontendUrl
    ) {
        this.jwtUtility = jwtUtility;
        this.cookieUtility = cookieUtility;
        this.frontendUrl = frontendUrl;
    }
    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException {

        CustomOAuthUser oauth2User =
                (CustomOAuthUser) authentication.getPrincipal();

        String email=oauth2User.getName();
        UUID userId=oauth2User.getUser().getId();
        UserRole role=oauth2User.getUser().getRole();

        String token= jwtUtility.generateToken(email,userId,role);
        ResponseCookie cookie = cookieUtility.buildAuthCookie(token);

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        response.sendRedirect(frontendUrl);
    }
}