package com.sibanarayan.code.utility;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;


@Component
public class CookieUtility {

    public static final String AUTH_COOKIE_NAME = "AUTH_TOKEN";

    private final boolean secure;
    private final String sameSite;
    private final String domain;
    private final long jwtExpirationMillis;

    public CookieUtility(
            @Value("${app.cookie.secure}") boolean secure,
            @Value("${app.cookie.same-site}") String sameSite,
            @Value("${app.cookie.domain:}") String domain,
            @Value("${app.jwt.expiration}") long jwtExpirationMillis
    ) {
        this.secure = secure;
        this.sameSite = sameSite;
        this.domain = domain;
        this.jwtExpirationMillis = jwtExpirationMillis;
    }

    public ResponseCookie buildAuthCookie(String token) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(AUTH_COOKIE_NAME, token)
                .httpOnly(true)
                .secure(secure)
                .sameSite(sameSite)
                .path("/")
                .maxAge(jwtExpirationMillis / 1000);

        if (domain != null && !domain.isBlank()) {
            builder.domain(domain);
        }

        return builder.build();
    }

    public ResponseCookie clearAuthCookie() {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(AUTH_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(secure)
                .sameSite(sameSite)
                .path("/")
                .maxAge(0);

        if (domain != null && !domain.isBlank()) {
            builder.domain(domain);
        }

        return builder.build();
    }
}
