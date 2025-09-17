package com.api.bandlogs_manager.entities;

import com.api.bandlogs_manager.enums.EventState;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;

import java.time.LocalDate;

import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.lang.NonNull;

/**
 * Project: bandlogs-manager
 * @author  Brando Eli Carrillo Perez
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "event_id")
    private String eventId;

    @Column(name = "event_date")
    @NonNull
    private LocalDate date;

    @NonNull
    private String description;
    /**From which creates many of these events (especifically by the director)**/
    @ManyToOne
    @JsonBackReference
    @JoinTable(name = "band_events",
            joinColumns = @JoinColumn(name = "event_id", referencedColumnName = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "band_id", referencedColumnName = "band_id"))
    private Band band;

    private String location;
    /** It represents into a serie, each song through title & tonality pair values**/
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @JoinTable(name = "events_songs",
            joinColumns = @JoinColumn(name = "event_id", referencedColumnName = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "song_id", referencedColumnName = "song_id"))
    private Set<Song> repertoire = new HashSet<Song>();

    private EventState state = EventState.PLANNED;

    public Event(LocalDate date, String description, String location, Set<Song> repertoire) {
        this.date = date;
        this.description = description;
        this.location = location;
        this.repertoire = repertoire;
    }

    public Event(LocalDate date, String description, Set<Song> repertoire) {
        this.date = date;
        this.description = description;
        this.repertoire = repertoire;
    }
}
