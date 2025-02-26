package com.api.bandlogs_manager.security.services;

import com.api.bandlogs_manager.entities.User;
import com.api.bandlogs_manager.repository.UserRepository;
import lombok.Getter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Objects;

/**
 * Project: bandlogs-manager
 * Author: Brando Eli Carrillo Perez
 */
@Service
public class BandMemberDetailsService implements UserDetailsService {
    @Getter
    private User userDetails;

    private final UserRepository userRepository;

    public BandMemberDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        userDetails = userRepository.findByNickname(username);

        if (!Objects.isNull(userDetails)) {
            return new org.springframework.security.core.userdetails.User(
                    userDetails.getNickname(),
                    userDetails.getPassword(),
                    new HashSet<>());
        } else {
            throw new UsernameNotFoundException("Usuario no encontrado");
        }
    }
}
