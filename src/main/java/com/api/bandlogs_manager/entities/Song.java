package com.api.bandlogs_manager.entities;

import com.api.bandlogs_manager.enums.Pitch;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Project: bandlogs-manager
 * Author: Brando Eli Carrillo Perez
 */
@NoArgsConstructor
@Entity
@Table(name = "songs")
@Data
public class Song {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "song_id")
    private int songId;

    @NonNull
    private String title;

    /** sound tone Pitch type enum value of this song **/
    @Enumerated(EnumType.ORDINAL)
    @NonNull
    private Pitch pitch;

    /** tonality suffix of this song, e.g.: 'Maj7' or 'm' or 'add9' **/
    @NonNull
    private String tonalitySuffix = "";

    /** 
     * List of all the chords with format name-suffix (joined by ';')
     * character, in which the music of the song is compound **/
    @Column(name = "progression")
    @ElementCollection
    @CollectionTable(
        name = "songs_progressions",
        joinColumns = @JoinColumn(
            name = "song_id",
            referencedColumnName = "song_id"))
    @NonNull
    private List<String> progression = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinTable(name = "events_songs",
        joinColumns = @JoinColumn(name = "song_id", referencedColumnName = "song_id"),
        inverseJoinColumns = @JoinColumn(name = "event_id", referencedColumnName = "event_id"))
    private List<Event> events;

    public Song(int songId,
                String title,
                Pitch pitch,
                String tonalitySuffix,
                List<String> progression) {
                    this.songId = songId;
                    this.title = title;
                    this.pitch = pitch;
                    this.tonalitySuffix = tonalitySuffix;
                    this.progression = progression;
                }

    public Song(String title,
                Pitch pitch,
                String tonalitySuffix,
                List<String> progression) {
                    this.title = title;
                    this.pitch = pitch;
                    this.tonalitySuffix = tonalitySuffix;
                    this.progression = progression;
                }
}
