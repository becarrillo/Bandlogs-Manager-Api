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
 * @author Brando Eli Carrillo Perez
 * 
 * Description: This service loads user details for band members and retrieves 
 * user information from the UserRepository and constructs a UserDetails object.
 */
@Service
public class BandMemberDetailsService implements UserDetailsService {
    @Getter
    private User userDetails;

    private final UserRepository userRepository;

    public BandMemberDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * This method is used to load user details by username (nickname).
     * It retrieves the user from the repository and constructs a UserDetails object with the user's role.
     * If the user is not found, it throws a UsernameNotFoundException.
     *
     * @param username The nickname of the user to be loaded.
     * @return UserDetails object containing user information and authorities.
     */
    @Override
    public UserDetails loadUserByUsername(String username) {
        userDetails = userRepository.findByNickname(username);
       
        final Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        if (!Objects.isNull(userDetails)) {
            final SimpleGrantedAuthority authority = new SimpleGrantedAuthority(userDetails.getRole().toString());
            // Add the role to the authorities set If the user is an ADMIN, add ROLE_USER as well This is to en-
            // sure that ADMIN users have both roles This is a common practice in role-based access control systems
            if (userDetails.getRole().toString().equals("ADMIN")) {
                authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                authorities.add(authority);
            } else {
                authorities.add(authority);
            }
            
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
