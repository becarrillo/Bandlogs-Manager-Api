package com.api.bandlogs_manager.controllers;

import com.api.bandlogs_manager.dtos.TonalityDTO;

import com.api.bandlogs_manager.entities.Event;
import com.api.bandlogs_manager.entities.Song;

import com.api.bandlogs_manager.services.SongService;

import java.util.List;

import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/repertorio")
public class SongController {
    private final SongService songService;

    public SongController(SongService songService) {
        this.songService = songService;
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

    @GetMapping(path = "/{songId}")
    public ResponseEntity<Song> getSongById(
        @PathVariable("songId") int id) 
    {
        try {
            final Song foundSongById = this.songService.getSongById(id);
            return new ResponseEntity<>(foundSongById, HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PatchMapping(path = "/{songId}/transportar")
    public ResponseEntity<Song> transportSong(
        @PathVariable("songId") int id,
        @RequestBody TonalityDTO dto) {
            Song foundSongById = null;
            try {
                final Song transportedSong = this.songService.transportSong(foundSongById, dto);
                return new ResponseEntity<>(
                        this.songService.updateSong(id, transportedSong),
                        HttpStatus.OK);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
    }

    @PutMapping(path = "/{songId}/modificar")
    public ResponseEntity<Song> updateSong(@PathVariable("songId") int id, @RequestBody Song song) {
        try {    
            final Song updatedSong = this.songService.updateSong(id, song);
            return new ResponseEntity<>(
                updatedSong,
                HttpStatus.OK
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping(path = "/eliminar")
    public ResponseEntity<Void> deleteSong(@RequestBody Song song) {
        try {
            this.songService.deleteSong(song); // delete song if logged-in user is director at least one band of its event
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}