package com.api.bandlogs_manager.controllers;

import com.api.bandlogs_manager.entities.Band;
import com.api.bandlogs_manager.entities.Event;
import com.api.bandlogs_manager.entities.User;

import com.api.bandlogs_manager.enums.UserRole;

import com.api.bandlogs_manager.exceptions.ResourceNotFoundException;

import com.api.bandlogs_manager.security.JwtUtil;

import com.api.bandlogs_manager.services.BandService;

import io.jsonwebtoken.Claims;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;

import java.nio.charset.StandardCharsets;

import java.util.Set;

/**
 * Project: bandlogs-manager
 * Author: Brando Eli Carrillo Perez
 */
@RestController
@RequestMapping("/api/v1/bandas")
public class BandController {
    private final BandService bandService;
    private final JwtUtil jwtUtil;

    public BandController(BandService bandService, JwtUtil jwtUtil) {
        this.bandService = bandService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/{bandId}")
    public ResponseEntity<Band> getBandById(@RequestHeader("Authorization") String authHeader, @PathVariable("bandId") Short id) {
        try {
            final Claims claims = this.jwtUtil.extractAllClaims(authHeader.replace("Bearer ", ""));
            final String loggedInUserNickname = claims.getSubject();
            final UserRole userRole = UserRole.valueOf(claims.get("role", String.class));
            final Band foundBand = this.bandService.getBandById(id);
            if (!(foundBand.getDirector().equals(loggedInUserNickname)  
                || foundBand.getUsers()
                    .stream()
                    .filter(u -> u.getNickname().equals(loggedInUserNickname))
                    .findFirst()
                    .isPresent())
                || userRole.equals(UserRole.ROLE_ADMIN)) {
                    // It handles the response when authenticated user is not member or director related with band
                    return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            return new ResponseEntity<>(foundBand, HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping(path = "/banda", params = {"nombre"})
    public ResponseEntity<Band> 
    getBandByName(@RequestHeader("Authorization") String authHeader,@RequestParam("nombre") String name) {
        try {
            final String authUsername = this.jwtUtil.extractUsername(
                authHeader.replace("Bearer ", "")); // get me authenticated user nickname by JWT
            final Band foundBand = this.bandService.getBandByName(name);
            if (!(foundBand.getDirector().equals(authUsername) 
                || foundBand.getUsers()
                    .stream()
                    .filter(u -> u.getNickname().equals(authUsername))
                    .findFirst()
                    .isPresent())) {
                        // It handles the response when authenticated user is not member or director related with band
                        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            return new ResponseEntity<>(foundBand, HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping
    public ResponseEntity<Set<Band>> listAllBands() {
        try {
            return new ResponseEntity<>(
                    this.bandService.getAllBandsSet(),
                    HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping(path = "/por-director", params = {"nombre-de-usuario"})
    public ResponseEntity<Set<Band>> listBandsByDirector(
        @RequestHeader("Authorization") String authHeader,
        @RequestParam("nombre-de-usuario") String nickname
    ) {
        try {
            final String loggedInUserNickname = this.jwtUtil.extractUsername(
                authHeader.replace("Bearer ", "")); // get me authenticated user nickname by JWT
            final Set<Band> bands = this.bandService.getBandsSetByDirectorAndLoggedInUserNicknames(
                URLDecoder.decode(nickname, StandardCharsets.UTF_8),
                loggedInUserNickname);
            return new ResponseEntity<>(bands, HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping(path = "/por-miembro", params = {"nombre-de-usuario"})
    public ResponseEntity<Set<Band>> listBandsByMemberUserNickname(
        @RequestHeader("Authorization") String authHeader,
        @RequestParam("nombre-de-usuario") String nickname
    ) {
        final String loggedInUserNickname = this.jwtUtil.extractUsername(
                authHeader.replace("Bearer ", "")); // get me authenticated user nickname by JWT
        Set<Band> bands;
        bands = this.bandService
                .getBandsSetByMemberUserAndLoggedInUserNicknames(
                    URLDecoder.decode(nickname, StandardCharsets.UTF_8),
                    loggedInUserNickname);
        try {
            return new ResponseEntity<>(
                bands,
                HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping(path = "/agregar")
    public ResponseEntity<Band> addBand(
        @RequestHeader("Authorization") String authHeader, @RequestBody Band band) {
            try {
                final String loggedInUserNickname = this.jwtUtil.extractUsername(
                    authHeader.replace("Bearer ", ""));// authenticated user nickname by JWT
                return new ResponseEntity<>(
                        this.bandService.saveBand(band, loggedInUserNickname),
                        HttpStatus.CREATED);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
    }

    @PatchMapping(path = "/{bandId}/eventos/agregar")
    public ResponseEntity<Band> patchEventToBand(
        @RequestHeader("Authorization") String authHeader,
        @PathVariable("bandId") short id,
        @RequestBody Event event
    ) {
        try {
            final String loggedInUserNickname = this.jwtUtil.extractUsername(
                authHeader.substring(7));   // get me authenticated user nickname by JWT
            final Band patchedBand = this.bandService.addEventToBand(id, event, loggedInUserNickname);
            return new ResponseEntity<>(
                patchedBand,
                HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PatchMapping(path = "/{bandId}/usuarios/agregar")
    public ResponseEntity<Band> patchMemberUserToBand(
        @RequestHeader("Authorization") String authHeader,
        @PathVariable("bandId") short id,
        @RequestBody User user
    ) {
        final String authUsername=this.jwtUtil.extractUsername(
            authHeader.substring(7));// get me authenticated user nickname by JWT
        final Band band = this.bandService.addMemberUserToBand(id, user, authUsername);
        if (band==null) {
            throw new RuntimeException(
                    "Error al agregar miembro a la banda: " +
                            "el usuario ya es miembro de la banda.");
        }
        return new ResponseEntity<>(band, HttpStatus.OK);
    }

    @DeleteMapping(path = "/eliminar")
    public ResponseEntity<Void> deleteBand(@RequestBody Band band) {
        try {
            this.bandService.deleteBand(band);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PutMapping(path = "/{bandId}/modificar")
    public ResponseEntity<Band> updateBandById(
        @RequestHeader("Authorization") String authHeader,
        @PathVariable("bandId") short id,
        @RequestBody Band band) {
            Band updatedBand = null;
            try {
                final String authUsername = this.jwtUtil.extractUsername(
                    authHeader.replace("Bearer ", "")); // get me authenticated user nickname by JWT
                updatedBand = this.bandService.updateBand(id, band, authUsername);
                return new ResponseEntity<>(updatedBand, HttpStatus.OK);
            } catch (ResourceNotFoundException e) {
                throw new RuntimeException(e);
            }
    }
}
