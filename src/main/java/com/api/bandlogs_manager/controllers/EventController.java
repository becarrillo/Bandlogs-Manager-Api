package com.api.bandlogs_manager.controllers;

import com.api.bandlogs_manager.entities.Band;
import com.api.bandlogs_manager.entities.Event;
import com.api.bandlogs_manager.exceptions.ResourceNotFoundException;
import com.api.bandlogs_manager.services.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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

    @GetMapping(path = "/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable String id) {
        return new ResponseEntity<Event>(
                this.eventService.getEventById(id),
                HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Event>> listAllEvents() {
        try {
            return new ResponseEntity<List<Event>>(
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
        return new ResponseEntity<List<Event>>(
                this.eventService.getEventsByDate(formattedDate),
                HttpStatus.OK);
    }

    @PostMapping(path = "/agregar")
    public ResponseEntity<Event> addEvent(@RequestBody Event event) {
        try {
            return new ResponseEntity<Event>(
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
            return new ResponseEntity<Void>(HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PutMapping(path = "/{eventId}/modificar")
    public ResponseEntity<Event> updateEvent(
            @PathVariable("eventId") String id,
            @RequestBody Event event) {

        return new ResponseEntity<Event>(
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
        return new ResponseEntity<Event>(event, HttpStatus.OK);
    }
}
