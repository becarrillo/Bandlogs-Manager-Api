package com.api.bandlogs_manager.security;

import com.api.bandlogs_manager.dto.UserDTO;
import com.api.bandlogs_manager.entities.User;
import com.api.bandlogs_manager.repository.UserRepository;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Project: bandlogs-manager
 * Author: Brando Eli Carrillo Perez
 */
@Component
public class JwtUtil {

    private String secret = "My-smart-SECRET";

    private final UserRepository userRepository;

    public JwtUtil(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostConstruct
    protected void onInit() {
        secret = Base64.getEncoder().encodeToString(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String createToken(UserDTO dto) {
        final User userEntity = userRepository.findByNickname(dto.getNickname());
        final Map<String, Object> claims = new HashMap<String, Object>();
        claims.put("userId", userEntity.getUserId());
        claims.put("phoneNumber", userEntity.getPhoneNumber());
        claims.put("role", userEntity.getRole());
        final Date now = new Date(System.currentTimeMillis());
        final Date expiration = new Date(now.getTime() + 900000L);
        return Jwts.builder()
                .setSubject(userEntity.getNickname())
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.ES256, secret)
                .compact();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final Claims allClaims = extractAllClaims(token);
        final User userEntity = userRepository.findByNickname(allClaims.getSubject());
        final JwtParser parser = Jwts.parser();
        return parser.isSigned(token)
                && userDetails.getUsername().equals(userEntity.getNickname())
                && userDetails.getPassword().equals(userEntity.getPassword())
                && !isTokenExpired(allClaims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser().parseClaimsJws(token).getBody();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean isTokenExpired(Claims claims) {
        return claims
                .getExpiration()
                .before(new Date());
    }
}
