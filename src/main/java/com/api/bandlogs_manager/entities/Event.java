package com.api.bandlogs_manager.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

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
    @JsonFormat(pattern = "YYYY-MM-dd")
    private Date eventDate;

    private String description;
    /**From which creates many of these events (especifically by the director)**/
    @ManyToOne
    @JoinColumn(name = "band_id", referencedColumnName = "band_id")
    private Band band;

    private String location;
    /** It represents into a serie, each song through title & tonality pair values**/
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "events_songs",
        joinColumns = @JoinColumn(name = "event_id", referencedColumnName = "event_id"),
        inverseJoinColumns = @JoinColumn(name = "song_id", referencedColumnName = "song_id"))
    private Set<Song> repertoire = new HashSet<Song>();

    public Event(Date eventDate, String description, String location) {
        this.eventDate = eventDate;
        this.description = description;
        this.location = location;
    }
}
