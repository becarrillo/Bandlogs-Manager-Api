package com.api.bandlogs_manager.services;

import java.util.Optional;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.api.bandlogs_manager.dtos.LoginUserDTO;
import com.api.bandlogs_manager.entities.User;
import com.api.bandlogs_manager.repository.UserRepository;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public AuthService(
        AuthenticationManager authenticationManager,
        PasswordEncoder passwordEncoder,
        UserRepository userRepository) {
            this.authenticationManager = authenticationManager;
            this.passwordEncoder = passwordEncoder;
            this.userRepository = userRepository;
    }

    public Boolean userIsAuthenticated(LoginUserDTO dto) {
        final String nickname = dto.getNickname();
        final String password = dto.getPassword();
        return login(nickname, password).isAuthenticated();
    }

    public Authentication login(String nickname, String password) {
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
    
    public User signUp(User user) {
        final Optional<User> optUser = this.userRepository.findByNickname(user.getNickname());
        if (optUser.isPresent())
            return null;
        user.setPassword(this.passwordEncoder.encode(user.getPassword()));
        return this.userRepository.save(user);
    }
}