package com.api.bandlogs_manager.services;

import com.api.bandlogs_manager.entities.User;
import com.api.bandlogs_manager.exceptions.ResourceNotFoundException;
import com.api.bandlogs_manager.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Project: bandlogs-manager
 * Author: Brando Eli Carrillo Perez
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserById(Integer id) {
        Optional<User> userOpt = this.userRepository.findById(id);
        return userOpt.orElseThrow(ResourceNotFoundException::new);
    }

    public User getUserByPhoneNumber(String phoneNumber) {
        final Optional<User> userOpt = this.userRepository.findByPhoneNumber(phoneNumber);
        return userOpt.orElse(null);
    }

    public User getUserByNickname(String nickname) {
        final Optional<User> userOpt = this.userRepository.findByNickname(nickname);
        return userOpt.orElse(null);
    }

    public List<User> getAllUsers() {
        return this.userRepository.findAll();
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