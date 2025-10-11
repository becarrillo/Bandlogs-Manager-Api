package com.api.bandlogs_manager.controllers;

import com.api.bandlogs_manager.dtos.TonalityDTO;

import com.api.bandlogs_manager.entities.Song;
import com.api.bandlogs_manager.exceptions.ResourceNotFoundException;
import com.api.bandlogs_manager.services.SongService;

import java.util.List;

import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

/**
 * Project: bandlogs-manager
 * Author: Brando Eli Carrillo Perez
 */
@RestController
@RequestMapping("/api/v1/repertorio")
public class SongController {
    private final SongService songService;

    public SongController(SongService songService) {
        this.songService = songService;
    }

    @GetMapping(path = "/{songId}")
    public ResponseEntity<Song> getSongById(
        @PathVariable("songId") int id) 
    {
        try {
            final Song foundSongById = this.songService.getSongById(id);
            return new ResponseEntity<>(foundSongById, HttpStatus.OK);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 403)
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            throw new RuntimeException(e);
        } catch (ResourceNotFoundException e) {
            throw e;
        }
    }

    @PutMapping(path = "/{songId}/transportar")
    public ResponseEntity<Song> transportSong(
        @PathVariable("songId") int id,
        @RequestBody TonalityDTO dto) {
            Song foundSongById = null;
            try {
                foundSongById = this.songService.getSongById(id);
                return new ResponseEntity<>(
                        this.songService.transportSong(foundSongById, dto),
                        HttpStatus.OK);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
    }

    @GetMapping
    public ResponseEntity<List<Song>> listAllSongs(@RequestHeader("Authorization") String authHeader) {
        try {
            final List<Song> songsList = this.songService.getSongsList(authHeader.replace("Bearer ", ""));
            return new ResponseEntity<>(songsList, HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}