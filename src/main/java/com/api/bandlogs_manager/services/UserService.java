package com.api.bandlogs_manager.services;

import com.api.bandlogs_manager.dtos.UserRoleDTO;

import com.api.bandlogs_manager.entities.User;

import com.api.bandlogs_manager.enums.UserRole;

import com.api.bandlogs_manager.exceptions.ResourceNotFoundException;

import com.api.bandlogs_manager.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.query.Param;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;


/**
 * Project: bandlogs-manager
 * Author: Brando Eli Carrillo Perez
 */
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User getUserById(Integer id) {
        Optional<User> userOpt = this.userRepository.findById(id);
        return userOpt.orElseThrow(ResourceNotFoundException::new);
    }

    public User getUserByPhoneNumber(String phoneNumber) {
        final Optional<User> userOpt = Optional.of(this.userRepository.findByPhoneNumber(phoneNumber));
        return userOpt.orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado por su número celular"));
    }

    public User getUserByNickname(String nickname) {
        final Optional<User> userOpt = Optional.of(this.userRepository.findByNickname(nickname));
        return userOpt.orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado por su nickname"));
    }

    public List<User> listAllUsers() {
        return this.userRepository.findAll();
    }

    public List<User> listUsersByNicknameContaining(String containing) {
        final Optional<List<User>> usersOpt = Optional.of(this.userRepository.findByNicknameContaining(containing));
        return usersOpt.orElseThrow(() -> new ResourceNotFoundException("No existe usuario alguno con nickname: ".concat(containing)));
    }

    public User registerUser(User user) {
        user.setPassword(this.passwordEncoder.encode(user.getPassword()));
        user.setRole(UserRole.ROLE_USER); // for secure purposes we must not allow to any user to be admin by himself
        return this.userRepository.save(user);
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    public User setUserRoleByUserId(Integer userId, UserRoleDTO dto) {
        // This method is a placeholder for setting the user role based on the access token.
        // The actual implementation would depend on the authentication and authorization logic.
        final Optional<User> userOpt = this.userRepository.findById(userId);
        if (userOpt.isPresent()) {
            if (!userOpt.get().getNickname().equals(dto.nickname)) {
                throw new IllegalArgumentException(
                    "El nickname esperado en el usuario recuperado no concuerda con el del cuerpo de petición (tipo UserRoleDTO)");
            }
            User user = userOpt.get();
            user.setRole(dto.role);
            return this.userRepository.saveAndFlush(user);
        }
        return null;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUserById(Integer userId) {
        this.userRepository.deleteById(userId);
    }

    @PreAuthorize("#u.nickname == authentication.name or hasRole('ADMIN')")
    public User updateUser(@Param("u") User olduser, User newuser) {
        if (!newuser.getPassword().equals(olduser.getPassword()) &&
            !this.passwordEncoder.matches(newuser.getPassword(), olduser.getPassword())) {
                newuser.setPassword(this.passwordEncoder.encode(newuser.getPassword()));
        } else {
            newuser.setPassword(olduser.getPassword());
        }
        return this.userRepository.saveAndFlush(newuser);
    }
}