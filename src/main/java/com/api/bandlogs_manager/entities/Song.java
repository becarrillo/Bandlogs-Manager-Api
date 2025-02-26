package com.api.bandlogs_manager.entities;

import com.api.bandlogs_manager.enums.Tonality;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Project: bandlogs-manager
 * Author: Brando Eli Carrillo Perez
 */
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "song")
@Data
public class Song {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "song_id")
    private int songId;

    private String title;
    /**A number that represents armony tonality in which the musicalization
     * of this is agreed, from 0 to 23 including minors tonalities**/
    @Enumerated(EnumType.ORDINAL)
    private Tonality tonality;

    @ManyToMany(fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinTable(name = "events_songs",
        joinColumns = @JoinColumn(name = "song_id", referencedColumnName = "song_id"),
        inverseJoinColumns = @JoinColumn(name = "event_id", referencedColumnName = "event_id"))
    private List<Event> events;
}
