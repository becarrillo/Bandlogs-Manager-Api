package com.api.bandlogs_manager.services;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.api.bandlogs_manager.entities.User;
import com.api.bandlogs_manager.repository.UserRepository;

import lombok.Getter;

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
        userDetails = userRepository.findByNickname(username).get();
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(userDetails.getRole().toString());
        final Set<SimpleGrantedAuthority> authorities = new HashSet<>();

        if (!Objects.isNull(userDetails)) {
            authorities.add(authority);
            
            return new org.springframework.security.core.userdetails.User(
                userDetails.getNickname(),
                userDetails.getPassword(),
                authorities
            );
        } else {
            throw new UsernameNotFoundException("Usuario no encontrado");
        }
    }
}
