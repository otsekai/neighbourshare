package com.example.springproject.component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtCore {
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.lifetime}")
    private long lifeTime;

    public String generateToken(String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + lifeTime);

        return Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSecretKey())
                .compact();
    }

    public String getEmailFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean isTokenValid(String token, UserDetails user) {
        final String email = getEmailFromToken(token);
        return email.equals(user.getUsername()) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration()
                .before(new Date());
    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
}
