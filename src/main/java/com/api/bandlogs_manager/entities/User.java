package com.api.bandlogs_manager.entities;

import com.api.bandlogs_manager.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;   // Only TESTING
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

import org.springframework.lang.NonNull;

/**
 * Project: bandlogs-manager
 * Author: Brando Eli Carrillo Perez
 */
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users", uniqueConstraints =
        {@UniqueConstraint(columnNames = {"nickname", "phone"})})
public class User {
    @Getter    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int userId;

    @Getter
    @Setter
    @NonNull
    private String firstname;

    @Getter
    @Setter
    @NonNull
    private String lastname;

    @Getter
    @Setter
    @NonNull
    private String nickname;

    @Getter
    @Setter
    @NonNull
    private String password;
    
    @Getter
    @Setter
    @NonNull
    @Column(name = "phone")
    private String phoneNumber;

    @Getter
    @Setter
    @NonNull
    private UserRole role;

    @Getter
    @Setter
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.REMOVE})
    @JsonBackReference
    @JoinTable(name = "users_bands",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "band_id", referencedColumnName = "band_id"))
    private Set<Band> bands = new HashSet<>();

    public User(
            String firstname,
            String lastname,
            String nickname,
            String password,
            String phoneNumber) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.nickname = nickname;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.role = UserRole.ROLE_USER;
    }
}
