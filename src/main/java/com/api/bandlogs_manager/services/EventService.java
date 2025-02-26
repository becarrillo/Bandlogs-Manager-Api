package com.api.bandlogs_manager.services;

import com.api.bandlogs_manager.entities.Band;
import com.api.bandlogs_manager.entities.Event;
import com.api.bandlogs_manager.exceptions.ResourceNotFoundException;
import com.api.bandlogs_manager.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.util.*;

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
        Optional<Event> EVENT_OPT = this.eventRepository.findById(id);
        return EVENT_OPT.orElseThrow(ResourceNotFoundException::new);
    }

    public List<Event> getAllEvents() {
        return this.eventRepository.findAll();
    }

    public List<Event> getEventsByDate(Date date) {
        try {
            return this.eventRepository.findAllByDate(date);
        } catch (Exception e) {
            throw new ResourceNotFoundException(e.getMessage());
        }
    }

    public Event saveEvent(Event event) {
        return this.eventRepository.save(event);
    }

    public void deleteEventById(String id) {
        this.eventRepository.deleteById(id);
    }

    public Event updateEvent(String id, Event event) {
        final Optional<Event> EVENT_OPT = this.eventRepository.findById(id);
        if (EVENT_OPT.isPresent() &&
                Objects.equals(event.getEventId(), EVENT_OPT.get().getEventId())) {
            return this.eventRepository.save(event);
        }
        return null;
    }

    public Event addBandToEvent(String id, Band band) {
        final Optional<Event> EVENT_OPT = this.eventRepository.findById(id);

        if (EVENT_OPT.isPresent()) {
            final Event event = EVENT_OPT.get();
            event.setBand(band);
            return this.eventRepository.save(event);
        }
        return null;
    }
}
