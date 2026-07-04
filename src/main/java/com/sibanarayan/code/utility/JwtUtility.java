package com.sibanarayan.code.utility;

import com.sibanarayan.shared_package.enums.UserRole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtility {

    private final Key key;
    private final long expiration;

    public JwtUtility(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration}") long expiration
    ) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("JWT secret is not set");
        }
        if (secret.getBytes().length < 32) {
            throw new IllegalStateException("JWT secret must be at least 32 characters/bytes long");
        }

        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expiration = expiration;
    }

    public long getExpirationMillis() {
        return expiration;
    }

    // Generate token
    public String generateToken(String email, UUID userId, UserRole role) {
        Instant now = Instant.now();

        return Jwts.builder()
                .setSubject(email)
                .claim("userId", userId)
                .claim("ROLE",role.name())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(expiration)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Extract email
    public String getEmail(String token) {
        return getClaims(token).getSubject();
    }

    public  String getRole(String token){
        return getClaims(token).get("ROLE",String.class);
    }

    // Extract userId
    public UUID getUserId(String token) {
        String id= getClaims(token).get("userId", String.class);
        return UUID.fromString(id);
    }

    // Validate token
    public boolean isTokenValid(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
    public UserPrincipal getCurrentUser(){
        return (UserPrincipal) SecurityContextHolder.
                getContext().
                getAuthentication().
                getPrincipal();
    }

    // Internal helpers
    private Jws<Claims> parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }

    private Claims getClaims(String token) {
        return parseToken(token).getBody();
    }
}

