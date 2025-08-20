package com.api.bandlogs_manager.services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.api.bandlogs_manager.dtos.LoginUserDTO;


@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;

    public AuthService(
        AuthenticationManager authenticationManager) {
            this.authenticationManager = authenticationManager;
    }

    public Boolean userIsAuthenticated(LoginUserDTO dto) {
        final String nickname = dto.nickname;
        final String password = dto.password;
        return login(nickname, password).isAuthenticated();
    }

    private Authentication login(String nickname, String password) {
        Authentication authentication = null;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(nickname, password)
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }

    public void logout() {
        if (SecurityContextHolder.getContext().getAuthentication()!=null)
            SecurityContextHolder.clearContext();
    }
    
    
}