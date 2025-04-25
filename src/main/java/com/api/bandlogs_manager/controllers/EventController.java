package com.api.bandlogs_manager.controllers;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.api.bandlogs_manager.entities.Band;
import com.api.bandlogs_manager.entities.Event;
import com.api.bandlogs_manager.exceptions.ResourceNotFoundException;
import com.api.bandlogs_manager.services.EventService;

/**
 * Project: bandlogs-manager
 * Author: Brando Eli Carrillo Perez
 */
@RestController
@RequestMapping("/api/v1/eventos")
public class EventController {
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping(path = "/{eventId}")
    public ResponseEntity<Event> getEventById(@PathVariable("eventId") String id) {
        return new ResponseEntity<>(
                this.eventService.getEventById(id),
                HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Event>> listAllEvents() {
        try {
            return new ResponseEntity<>(
                    this.eventService.getAllEvents(),
                    HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping(path = {"date"})
    public ResponseEntity<List<Event>> listByDate(@RequestParam String date) {
        String dateAsString = URLDecoder
                .decode(date, StandardCharsets.UTF_8);
        final SimpleDateFormat FORMATTER =
                new SimpleDateFormat("yyyy-MM-dd");
        Date formattedDate = null;
        try {
            formattedDate = FORMATTER.parse(dateAsString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return new ResponseEntity<>(
                this.eventService.getEventsByDate(formattedDate),
                HttpStatus.OK);
    }

    @PostMapping(path = "/agregar")
    public ResponseEntity<Event> addEvent(@RequestBody Event event) {
        try {
            return new ResponseEntity<>(
                    this.eventService.saveEvent(event),
                    HttpStatus.CREATED);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping(path = "/{eventId}/eliminar")
    public ResponseEntity<Void> deleteEventById(
            @PathVariable("eventId") String id) {

        try {
            this.eventService.deleteEventById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PutMapping(path = "/{eventId}/modificar")
    public ResponseEntity<Event> updateEvent(
            @PathVariable("eventId") String id,
            @RequestBody Event event) {

        return new ResponseEntity<>(
                this.eventService.updateEvent(id, event),
                HttpStatus.OK);
    }

    @PatchMapping(path = "/{eventId}/bandas/agregar")
    public ResponseEntity<Event> asociateBandToEvent(
            @PathVariable("eventId") String id,
            @RequestBody Band band) {

        Event event = this.eventService.addBandToEvent(
                URLDecoder.decode(id, StandardCharsets.UTF_8), band);
        if (event==null) {
            throw new ResourceNotFoundException(
                    "Evento no existente con el id provisto");
        }
        return new ResponseEntity<>(event, HttpStatus.OK);
    }
}
