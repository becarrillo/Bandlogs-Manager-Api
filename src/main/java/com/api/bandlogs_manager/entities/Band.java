package com.api.bandlogs_manager.entities;

import com.api.bandlogs_manager.enums.MusicalGenre;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.lang.NonNull;

import java.util.List;

import jdk.jfr.Unsigned;

/**
 * Project: bandlogs-manager
 * Author: Brando Eli Carrillo Perez
 */
@NoArgsConstructor
@Data
@Entity
@Table(name = "bands", uniqueConstraints = @UniqueConstraint(columnNames = {"name"}))
public class Band {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Unsigned
    @Column(name = "band_id")
    private Short bandId;

    private String name;

    @Column(name = "director_nickname")
    @NonNull
    private String director;
    
    @ManyToMany(fetch = FetchType.LAZY)         // Db entity relationship
    @JoinTable(name = "users_bands",
            joinColumns = @JoinColumn(name = "band_id", referencedColumnName = "band_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "user_id"))
    private List<User> users;

    @Enumerated(EnumType.STRING)
    @Column(name = "musical_genre")
    @NonNull
    private MusicalGenre musicalGenre;

    @OneToMany(fetch = FetchType.LAZY)   // Db entity relationship
    @JsonManagedReference
    @JoinTable(name = "band_events",
            joinColumns = @JoinColumn(name = "band_id", referencedColumnName = "band_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id", referencedColumnName = "event_id"))
    private List<Event> events;

    public Band(
            String name,
            MusicalGenre musicalGenre
    ) {
        this.name = name;
        this.musicalGenre = musicalGenre;
    }
}
