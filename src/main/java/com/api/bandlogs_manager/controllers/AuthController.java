package com.api.bandlogs_manager.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.api.bandlogs_manager.dtos.LoginUserDTO;
import com.api.bandlogs_manager.dtos.TokenDTO;
import com.api.bandlogs_manager.dtos.UserRoleDTO;

import com.api.bandlogs_manager.enums.UserRole;
import com.api.bandlogs_manager.security.JwtUtil;
import com.api.bandlogs_manager.services.AuthService;

import io.jsonwebtoken.Claims;

/**
 * Project: bandlogs-manager
 * Author: Brando Eli Carrillo Perez
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil =  jwtUtil;
    }

    @PostMapping(path = "/login")
    public ResponseEntity<TokenDTO> login(@RequestBody(required = true) LoginUserDTO dto) {
        if (this.authService.userIsAuthenticated(dto)) {
            return new ResponseEntity<>(
                new TokenDTO(this.jwtUtil.createToken(dto.nickname)),
                HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    /**
     * Endpoint to get the UserRoleDTO type object by user ID.
     * @param userId The ID of the user.
     * @param authBearer The authorization header containing the JWT token necessary to extract
     * claims like the nickname or the role or also the userId.
     * The token should be in the format "Bearer mY-3XAMpL3-ToK3N".
     * @return A ResponseEntity containing the UserRoleDTO with the user's nickname and role.
     */
    @GetMapping(path = "/usuario-rol", headers = {"Authorization"})
    public ResponseEntity<UserRoleDTO> getAuthenticatedUserRole(@RequestHeader("Authorization") String authHeader) {
            final String token = authHeader.replace("Bearer ", "");

            if (token.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            String loggedInUserNickname;
            UserRole userRole;
            try {
                final Claims claims = this.jwtUtil.extractAllClaims(token);
                loggedInUserNickname = claims.getSubject();  // Extracting the subject username (nickname) from the token
                userRole = UserRole.valueOf(claims.get("role", String.class));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return new ResponseEntity<>(
                UserRoleDTO
                    .builder()
                    .nickname(loggedInUserNickname)
                    .role(userRole)
                    .build(),
                HttpStatus.OK);
    }

    public ResponseEntity<Void> logout() {
        try {
            this.authService.logout();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}