package com.api.bandlogs_manager.services;

import com.api.bandlogs_manager.entities.Event;
import com.api.bandlogs_manager.entities.Song;

import com.api.bandlogs_manager.exceptions.ResourceNotFoundException;
import com.api.bandlogs_manager.repository.EventRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDate;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Project: bandlogs-manager
 * Author: Brando Eli Carrillo Perez
 */
@Service
public class EventService {
    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public Event getEventById(String id) {
        Optional<Event> eventOpt = this.eventRepository.findById(id);
        return eventOpt.orElseThrow(() -> new ResourceNotFoundException());
    }

    public List<Event> listAllEvents() {
        return this.eventRepository.findAll();
    }

    /** 
     * this method obtains all scheduled events to a particular date.
     * @param date particular local date to search in the events */
    public List<Event> listEventsByDate(LocalDate date) {
        return this.eventRepository.findByDate(date);
    }

    public Event saveEvent(Event event) {
        return this.eventRepository.save(event);
    }

    public void deleteEventById(String eventId) {
        this.eventRepository.deleteById(eventId);
    }

    public Event updateEventById(String id, Event event) {
        final Optional<Event> eventOpt = this.eventRepository.findById(id);
        if (eventOpt.isPresent() && !event.getEventId().equals(eventOpt.get().getEventId()))
            throw new IllegalArgumentException(
                "el id del argumento evento no concuerda ".concat(
                    "con el id de evento a modificar en la variable de la ruta"));
        if (eventOpt.isEmpty())
            throw new ResourceNotFoundException("evento no encontrado con el id sugerido en la consulta");
        return this.eventRepository.save(event);
    }

    public Event addSongToEvent(String eventId, Song song) {
        final Optional<Event> eventOpt = this.eventRepository.findById(eventId);
        if (eventOpt.isEmpty())
            throw new ResourceNotFoundException("evento no encontrado con el id sugerido en la consulta");
        final Event event = eventOpt.get();
        final Set<Song> repertoire = event.getRepertoire();
        repertoire.add(song);
        return this.eventRepository.save(event);
    }
}
