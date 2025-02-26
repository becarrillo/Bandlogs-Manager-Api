package com.api.bandlogs_manager.services;

import com.api.bandlogs_manager.entities.User;
import com.api.bandlogs_manager.exceptions.ResourceNotFoundException;
import com.api.bandlogs_manager.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Project: bandlogs-manager
 * Author: Brando Eli Carrillo Perez
 */
@Service
public class UserService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;

    public UserService(final UserRepository userRepository, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
    }

    public User getUserById(Integer id) {
        Optional<User> userOpt = this.userRepository.findById(id);
        return userOpt.orElseThrow(ResourceNotFoundException::new);
    }

    public User getUserByPhoneNumber(String phoneNumber) {
        final Optional<User> userOpt = Optional
                .of(this.userRepository.findByPhoneNumber(phoneNumber));
        return userOpt.orElse(null);
    }

    public User getUserByNickname(String nickname) {
        final Optional<User> userOpt = Optional
                .of(this.userRepository.findByNickname(nickname));
        return userOpt.orElse(null);
    }

    public List<User> getAllUsers() {
        return this.userRepository.findAll();
    }

    public Boolean userIsAuthenticated(Map<String, Object> requestMap) {
        final String nickname = (String) requestMap.get("nickname");
        final String password = (String) requestMap.get("password");
        return login(nickname, password).isAuthenticated();
    }

    private Authentication login(String nickname, String password) {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(nickname, password)
        );
    }

    public User signUp(User user) {
        final User foundUser = this.userRepository.findByPhoneNumber(user.getPhoneNumber());
        if (Objects.nonNull(foundUser))
            return null;
        return this.userRepository.save(user);
    }

    public void deleteUserById(Integer id) {
        this.userRepository.deleteById(id);
    }

    public User updateUser(int id, User user) {
        final Optional<User> personOpt = this.userRepository.findById(id);
        if (personOpt.isPresent()) {
            return this.userRepository.save(user);
        }
        return  null;
    }
}