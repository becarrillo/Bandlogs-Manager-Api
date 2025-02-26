package com.api.bandlogs_manager.services;

import com.api.bandlogs_manager.entities.Band;
import com.api.bandlogs_manager.entities.User;
import com.api.bandlogs_manager.exceptions.ResourceNotFoundException;
import com.api.bandlogs_manager.repository.BandRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Project: bandlogs-manager
 * Author: Brando Eli Carrillo Perez
 */
@Service
public class BandService {
    private final BandRepository bandRepository;

    public BandService(BandRepository bandRepository) {
        this.bandRepository = bandRepository;
    }

    public Band getBandById(Short id) {
        Optional<Band> bandOpt = this.bandRepository.findById(id);
        return bandOpt.orElseThrow(ResourceNotFoundException::new);
    }

    public Set<Band> getAllBands() {
        // Convert from List to Set and later return it
        final Set<Band> bandsSet = new HashSet<Band>();
        bandsSet.addAll(this.bandRepository.findAll());
        return bandsSet;
    }

    public Band getBandByEventId(String eventId) {
        final Set<Band> bands = this.bandRepository.findAll()
                .stream()
                .collect(Collectors.toSet());
        if (bands.isEmpty())
            return null;
        Band foundBand = null;
        for (Band b : bands) {
             foundBand = b.getEvents()
                    .stream()
                    .filter(e -> e.getBand()==b)
                    .findAny()
                    .get()
                    .getBand();
        }
        return foundBand;
    }

    public Set<Band> getBandsByPersonId(Integer personId) {
        Set<Band> bandsByPersonId = new HashSet<Band>();
        for (Band band : this.getAllBands()) {
             final Optional<User> PERSON_OPT = band.getUsers()
                    .stream()
                    .filter(b -> b.getUserId()==personId)
                     .findFirst();
             if (PERSON_OPT.isPresent())
                 bandsByPersonId.add(band);
        }
        return bandsByPersonId;
    }

    public Band saveBand(Band band) {
        return this.bandRepository.save(band);
    }

    public void deleteBandyId(Short id) {
        this.bandRepository.deleteById(id);
    }

    public Band updateBand(Short id, Band band) {
        final Band FOUND_BAND = getBandById(id);
        return this.bandRepository.save(FOUND_BAND);
    }

    public Band addPersonToBand(Short bandId, User user) {
        final Band band = getBandById(bandId);
        final List<User> bandUsers = band.getUsers();

        if (bandUsers
                .stream()
                .noneMatch(b ->
                        b.getUserId()== user.getUserId())) {
            bandUsers.add(user);
            band.setUsers(bandUsers);
        }
        return this.bandRepository.save(band);
    }
}
