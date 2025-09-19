package com.api.bandlogs_manager.repository;

import com.api.bandlogs_manager.entities.User;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Project: bandlogs-manager
 * Author: Brando Eli Carrillo Perez
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByPhoneNumber(String phoneNumber);
    User findByNickname(String nickname);
}
