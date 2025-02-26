package com.api.bandlogs_manager.entities;

import com.api.bandlogs_manager.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * Project: bandlogs-manager
 * Author: Brando Eli Carrillo Perez
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "users", uniqueConstraints =
        {@UniqueConstraint(columnNames = {"nickname", "phone"})})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int userId;

    private String firstname;
    private String lastname;
    private String nickname;

    @Column(name = "phone")
    private String phoneNumber;

    private String password;
    private UserRole role;
    @ManyToMany(cascade = CascadeType.ALL)
    @JsonBackReference
    @JoinTable(name = "users_bands",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "band_id", referencedColumnName = "band_id"))
    private Set<Band> bands = new HashSet<>();

    public User(
            String firstname,
            String lastname,
            String nickname,
            String phoneNumber,
            String password) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }
}
