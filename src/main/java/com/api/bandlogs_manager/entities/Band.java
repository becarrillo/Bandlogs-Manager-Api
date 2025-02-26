package com.api.bandlogs_manager.entities;

import com.api.bandlogs_manager.enums.MusicalGenre;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Project: bandlogs-manager
 * Author: Brando Eli Carrillo Perez
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
@Table(name = "bands", uniqueConstraints = @UniqueConstraint(columnNames = {"name"}))
public class Band {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "band_id")
    private Short bandId;

    private String name;
    @Column(name = "director_person_id")
    private int directorUserId;
    // Db entity relationship
    @ManyToMany
    @JoinTable(name = "users_bands",
            joinColumns = @JoinColumn(name = "band_id", referencedColumnName = "band_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "user_id"))
    private List<User> users;

    @Enumerated(EnumType.STRING)
    @Column(name = "musical_genre")
    private MusicalGenre musicalGenre;

    @OneToMany(mappedBy = "band",
            cascade =  CascadeType.ALL,
            fetch = FetchType.LAZY)  // Db entity relationship
    @JsonBackReference
    private List<Event> events;

    public Band(
            String name,
            int directorUserId,
            MusicalGenre musicalGenre
    ) {
        this.name = name;
        this.directorUserId = directorUserId;
        this.musicalGenre = musicalGenre;
    }
}
