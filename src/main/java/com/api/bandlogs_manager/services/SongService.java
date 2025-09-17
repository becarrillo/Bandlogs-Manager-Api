package com.api.bandlogs_manager.services;

import com.api.bandlogs_manager.dtos.TonalityDTO;

import com.api.bandlogs_manager.entities.Band;
import com.api.bandlogs_manager.entities.Event;
import com.api.bandlogs_manager.entities.Song;

import com.api.bandlogs_manager.enums.Pitch;
import com.api.bandlogs_manager.enums.UserRole;

import com.api.bandlogs_manager.exceptions.ResourceNotFoundException;

import com.api.bandlogs_manager.repository.SongRepository;

import com.api.bandlogs_manager.security.JwtUtil;

import io.jsonwebtoken.Claims;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatusCode;

import org.springframework.stereotype.Service;

import org.springframework.web.client.HttpClientErrorException;


@Service
public class SongService {
    private final SongRepository songRepository;
    private final JwtUtil jwtUtil;

    public SongService(SongRepository songRepository, JwtUtil jwtUtil) {
        this.songRepository = songRepository;
        this.jwtUtil = jwtUtil;
    }

    public List<Song> getSongsList(String token) {
        final Claims claims = this.jwtUtil.extractAllClaims(token);
        final String loggedInUserNickname = claims.getSubject();
        final Set<Band> bands = new HashSet<>();
        final List<Event> events = new ArrayList<>();

        final List<Song> songs = this.songRepository.findAll();
        songs.stream().forEach(s -> s.getEvents().forEach(event -> events.add(event)));

        events.stream().map(e -> e.getBand()).forEach(b -> bands.add(b));
        
        boolean conditionA; // the logged-in user must to be a director of related band in all events (related to songs)
        conditionA = bands.stream().filter(b -> b.getDirector().equals(loggedInUserNickname)).findFirst().isPresent();    
        boolean conditionB =  // the logged-in user must to be a user with 'ROLE_ADMIN' role authority
        bands
            .stream()
            .filter(b -> 
                    b.getUsers()
                        .stream()
                        .filter(u -> u.getNickname().equals(loggedInUserNickname) || 
                                claims.get("role", UserRole.class)
                                    .equals(UserRole.ROLE_ADMIN))
                                    .findFirst()
                                    .isPresent())
                        .findFirst()
                        .isPresent();
        if (!(conditionA || conditionB))
            throw new HttpClientErrorException(HttpStatusCode.valueOf(401));        // UNAUTHORIZED
        return songs;
    }
    
    public Song getSongById(Integer id) {
        final Optional<Song> songOpt = this.songRepository.findById(id);
        return songOpt.orElseThrow(ResourceNotFoundException::new);
    }

    public List<Event> filterSongEventsByAuthorizedBandDirector(List<Event> events, String loggedInUserNickname) {
        final List<Event> filteredEvents = events
            .stream()
            .filter(event -> event.getBand().getDirector().equals(loggedInUserNickname))
            .collect(Collectors.toList());
        return filteredEvents;
    }

    public List<Event> filterSongEventsByRelatedBandMemberUser(List<Event> events, String loggedInUserNickname) {
        final List<Event> filteredEvents = events
            .stream()
            .filter(event -> event
                .getBand()
                .getUsers()
                .stream()
                .filter(u -> u.getNickname().equals(loggedInUserNickname)).findFirst().isPresent())
            .collect(Collectors.toList());
        return filteredEvents;
    }

    public Song saveSong(Song song) {
        if (song.getTonalitySuffix()==null || song.getTonalitySuffix()=="")
            song.setTonalitySuffix(" ");// because Oracle DB persists empty string as null
        return this.songRepository.save(song);
    }

    public Song updateSong(int id, Song song) {
        if (song.getSongId()!=id)
            throw new IllegalArgumentException();
        return this.songRepository.saveAndFlush(song);
    }
    
    public Song transportSong(Song song, TonalityDTO newTonality) {
        final int originPitchOrdinal = song.getPitch().ordinal();
        final int destPitchOrdinal = newTonality.pitch.ordinal();

        int semitones;
        if (destPitchOrdinal < originPitchOrdinal) { // Calculate the difference between chord enums ordinal
            semitones = Math.negateExact(originPitchOrdinal - destPitchOrdinal);
        } else {
            semitones = destPitchOrdinal - originPitchOrdinal;
        }

        final List<String> progression = song.getProgression();
        for (int i=0; i<progression.size(); i++) {
            TonalityDTO tonality;

            final String[] progressionSplit = progression.get(i).split(";");
            if (progressionSplit.length<2) {
                tonality = TonalityDTO.builder()
                        .pitch(Pitch.valueOf(progressionSplit[0]))// set pitch that is just before ';' chord string separator
                        .suffix("")  // set suffix to an empty string because originally chord doesn't have suffix
                        .build();
            } else {
                tonality = TonalityDTO.builder()
                        .pitch(Pitch.valueOf(progressionSplit[0]))// set pitch that is just before ';' chord string separator
                        .suffix(progressionSplit[1])  // set suffix that is just after ';' chord string separator
                        .build();
            }

            tonality.transport(semitones);  // set pitch to current as new song tonality comparison requires
            progression.set(i, tonality.pitch.toString().concat(";"+tonality.suffix));
        }

        song.setPitch(newTonality.pitch);
        song.setProgression(progression);
        return songRepository.save(song);
    }

    public void deleteSong(Song song) {
        this.songRepository.delete(song);
    }
}
