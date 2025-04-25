package com.api.bandlogs_manager.controllers;

import com.api.bandlogs_manager.entities.Band;
import com.api.bandlogs_manager.entities.User;
import com.api.bandlogs_manager.exceptions.ResourceNotFoundException;
import com.api.bandlogs_manager.services.BandService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * Project: bandlogs-manager
 * Author: Brando Eli Carrillo Perez
 */
@RestController
@RequestMapping("/api/v1/bandas")
public class BandController {
    private final BandService bandService;

    public BandController(BandService bandService) {
        this.bandService = bandService;
    }

    @GetMapping("/{bandId}")
    public ResponseEntity<Band> getBandById(@PathVariable("bandId") Short id) {
        final Band foundBand = this.bandService.getBandById(id);
        return new ResponseEntity<>(foundBand, HttpStatus.OK);
    }

    @GetMapping(path = "/bandas", params = {"event"})
    public ResponseEntity<Band> getBandByEventId(
            @PathVariable("eventId") String id,
            @RequestParam("event") String eventId) {

        final Band foundBand = this.bandService.getBandByEventId(eventId);
        if (foundBand==null) {
            throw new ResourceNotFoundException();
        }
        return new ResponseEntity<>(
                foundBand,
                HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Set<Band>> listAllBands() {
        try {
            return new ResponseEntity<>(
                    this.bandService.getAllBands(),
                    HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping(path = "/agregar")
    public ResponseEntity<Band> addBand(@RequestBody Band band) {
        try {
            return new ResponseEntity<>(
                    this.bandService.saveBand(band),
                    HttpStatus.CREATED);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PatchMapping(path = "/{bandId}/personas/agregar")
    public ResponseEntity<Band> addPersonToBand(
            @PathVariable("bandId") short id,
            @RequestBody User user
    ) {
        Band band = this.bandService.addPersonToBand(id, user);
        if (band==null) {
            throw new ResourceNotFoundException(
                    "Error en verificación de la banda: no existe el "+
                            "objeto a través de su id, la inserción de "+
                            "persona no puede ser efectuada en una instan-"+
                            " cia que no existe");
        }
        return new ResponseEntity<>(band, HttpStatus.OK);
    }

    @DeleteMapping(path = "{bandId}/eliminar")
    public ResponseEntity<Void> deleteBandById(@PathVariable("bandId") short id) {

        try {
            this.bandService.deleteBandyId(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PutMapping(path = "/{bandId}/modificar")
    public ResponseEntity<Band> updateBand(
            @PathVariable("bandId") Short id,
            @RequestBody Band band) {
        final Band updatedBand = this.bandService.updateBand(id, band);
        if (updatedBand==null)
            throw new ResourceNotFoundException(
                    "Id provisto no representa una instancia de Banda existente");
        try {
            return new ResponseEntity<>(band, HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
