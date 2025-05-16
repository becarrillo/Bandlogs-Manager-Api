package com.api.bandlogs_manager.controllers;

import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.bandlogs_manager.dtos.LoginUserDTO;
import com.api.bandlogs_manager.dtos.TokenDTO;
import com.api.bandlogs_manager.entities.User;
import com.api.bandlogs_manager.security.JwtUtil;
import com.api.bandlogs_manager.services.AuthService;

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
                new TokenDTO(this.jwtUtil.createToken(dto)),
                HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @PostMapping(path = "/registro")
    public ResponseEntity<User> signUp(@RequestBody User user) {
        final User savedUser = this.authService.signUp(user);
        if (Objects.isNull(savedUser))
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        try {
            return new ResponseEntity<>(
                    savedUser,
                    HttpStatus.CREATED);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}