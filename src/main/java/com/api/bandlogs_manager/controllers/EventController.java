package com.api.bandlogs_manager.controllers;

import java.net.URLDecoder;

import java.nio.charset.StandardCharsets;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.client.HttpClientErrorException;

import com.api.bandlogs_manager.entities.Band;
import com.api.bandlogs_manager.entities.Event;
import com.api.bandlogs_manager.entities.Song;

import com.api.bandlogs_manager.exceptions.ResourceNotFoundException;

import com.api.bandlogs_manager.security.JwtUtil;

import com.api.bandlogs_manager.services.BandService;
import com.api.bandlogs_manager.services.EventService;
import com.api.bandlogs_manager.services.SongService;

import io.jsonwebtoken.Claims;

import java.util.Optional;
import java.util.stream.Collectors;

import com.api.bandlogs_manager.enums.UserRole;

/**
 * Project: bandlogs-manager
 * Author: Brando Eli Carrillo Perez
 */
@RestController
@RequestMapping("/api/v1/eventos")
public class EventController {
    private final EventService eventService;
    private final BandService bandService;
    private final SongService songService;
    private final JwtUtil jwtUtil;

    public EventController(EventService eventService, BandService bandService, SongService songService,
            JwtUtil jwtUtil) {
        this.eventService = eventService;
        this.bandService = bandService;
        this.songService = songService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping(path = "/{eventId}")
    public ResponseEntity<Event> getEventById(@PathVariable("eventId") String id) {
        Event foundEvent;
        try {
            foundEvent = this.eventService.getEventById(id);
            return new ResponseEntity<>(
                    foundEvent,
                    HttpStatus.OK);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 403)
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            throw new RuntimeException(e);
        } catch (ResourceNotFoundException e) {
            throw e;
        }
    }

    @GetMapping
    public ResponseEntity<List<Event>> listAllEvents(@RequestHeader("Authorization") String authHeader) {
        try {
            UserRole userRole = UserRole.valueOf(
                    this.jwtUtil
                            .extractAllClaims(authHeader.replace("Bearer ", ""))
                            .get("role", String.class));
            final List<Event> events = this.eventService.listAllEvents();
            if (!userRole.equals(UserRole.ROLE_ADMIN)) // Only users with role admin are authorizated
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            return new ResponseEntity<>(
                    events,
                    HttpStatus.OK);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 403)
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping(path = "/", params = { "fecha" })
    public ResponseEntity<List<Event>> listEventsByDate(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("fecha") String date) {
        List<Event> events;
        final String dateAsString = URLDecoder
                .decode(date, StandardCharsets.UTF_8);
        try {
            final Claims claims = this.jwtUtil.extractAllClaims(authHeader.replace("Bearer ", ""));
            events = this.eventService
                    .listEventsByDate(LocalDate.parse(dateAsString))
                    .stream()
                    .filter(event -> event
                            .getBand()
                            .getUsers()
                            .stream()
                            .anyMatch(u -> u // subject in claims = logged-in user nickname
                                    .getNickname().equals(claims.getSubject()) // logged-in user is a related band
                                                                               // member
                                    || u.getRole().equals(UserRole.ROLE_ADMIN))) // or the logged-in user is admin
                    .collect(Collectors.toList());
            return new ResponseEntity<>(
                    events,
                    HttpStatus.OK);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 403)
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping(path = "/{eventId}/eliminar")
    public ResponseEntity<Void> deleteEventById(@RequestHeader("Authorization") String authHeader,
            @PathVariable("eventId") String id) {
        try {
            final String authUsername = this.jwtUtil.extractUsername(
                    authHeader.replace("Bearer ", "")); // get me authenticated user nickname by JWT
            final Event foundEvent = this.eventService.getEventById(id);
            final Optional<Band> bandOpt = this.bandService
                    .getBandsSetByDirectorAndLoggedInUserNicknames(authUsername, authUsername)
                    .stream()
                    .filter(b -> b.getEvents().contains(foundEvent))
                    .findFirst();
            // Ensure only to band director wich the related event to delete it
            if (bandOpt.isEmpty())
                throw new HttpClientErrorException(HttpStatusCode.valueOf(403));
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
        try {
            return new ResponseEntity<>(
                    this.eventService.updateEventById(id, event),
                    HttpStatus.OK);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 403)
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}