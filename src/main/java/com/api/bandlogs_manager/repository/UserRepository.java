package com.api.bandlogs_manager.repository;

import com.api.bandlogs_manager.entities.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Project: bandlogs-manager
 * Author: Brando Eli Carrillo Perez
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByPhoneNumber(String phoneNumber);
    User findByNickname(String nickname);
    /**
     * Search user by containing nickname substring where is not be empty, and it's equal to 
     * uppercase and trim value from client  **/
    @Query("SELECT u FROM User u WHERE LENGTH(:nickname)!=0 AND LOWER(TRIM(u.nickname)) LIKE %:nickname% ")
    List<User> findByNicknameContaining(@Param("nickname") String containing);
}
