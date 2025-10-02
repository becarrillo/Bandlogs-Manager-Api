package com.api.bandlogs_manager.security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.api.bandlogs_manager.entities.User;

import com.api.bandlogs_manager.repository.UserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Project: bandlogs-manager
 * Author: Brando Eli Carrillo Perez
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    private final UserRepository userRepository;

    public JwtUtil(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String createToken(String nickname) {
        final User userEntity = userRepository.findByNickname(nickname);
        final Map<String, Object> claims = new HashMap<>();
        claims.put("id", userEntity.getUserId());
        claims.put("role", userEntity.getRole().toString());
        final Date now = new Date(System.currentTimeMillis());
        final Date expiration = new Date(now.getTime() + 7200000L);
        final SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.builder()
                .subject(userEntity.getNickname())
                .claims(claims)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(key)
                .compact();
    }
    
    public boolean validateToken(String token, UserDetails userDetails) {
        final Claims allClaims = extractAllClaims(token);
        boolean isSigned = false;
        final SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
        try {
            isSigned = Jwts.parser().verifyWith(key).build().isSigned(token) 
                && extractUsername(token).equals(userDetails.getUsername())
                && !isTokenExpired(allClaims);
        } catch (Exception e) {
            System.out.println(e);
        }
        return isSigned;
    }

    public Claims extractAllClaims(String token) {
        final SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
        return (Claims) Jwts.parser().verifyWith(key).build().parse(token).getPayload();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    private boolean isTokenExpired(Claims claims) {
        return claims
                .getExpiration()
                .before(new Date());
    }
}
