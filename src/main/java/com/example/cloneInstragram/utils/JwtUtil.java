package com.example.cloneInstragram.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final long TOKEN_VALIDITY = 1000 * 60 * 60; // 1 час

    public String generateToken(String username) {
        Date now = new Date(System.currentTimeMillis());
        Date expiryDate = new Date(System.currentTimeMillis() + TOKEN_VALIDITY);

        System.out.println("Generating token for user: " + username);
        System.out.println("Current time (iat): " + now.getTime() + " (" + now + ")");
        System.out.println("Expiration time (exp): " + expiryDate.getTime() + " (" + expiryDate + ")");

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SECRET_KEY)
                .compact();
    }

    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    public boolean validateToken(String token, String username) {
        String extractedUsername = extractUsername(token);
        boolean isExpired = isTokenExpired(token);
        System.out.println("Validating token for user: " + username);
        System.out.println("Extracted username: " + extractedUsername);
        System.out.println("Token expired: " + isExpired);
        return (username.equals(extractedUsername) && !isExpired);
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }
}