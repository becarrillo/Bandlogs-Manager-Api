package com.api.bandlogs_manager.services;

import com.api.bandlogs_manager.entities.Band;
import com.api.bandlogs_manager.entities.Event;
import com.api.bandlogs_manager.entities.User;


import com.api.bandlogs_manager.exceptions.ResourceNotFoundException;

import com.api.bandlogs_manager.repository.BandRepository;

import org.springframework.data.repository.query.Param;

import org.springframework.http.HttpStatusCode;

import org.springframework.web.client.HttpClientErrorException;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.stereotype.Service;

import java.lang.IllegalArgumentException;

import java.util.stream.Collectors;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

    public Band getBandByName(String name) {
        final Optional<Band> bandOpt = Optional.of(this.bandRepository.findByName(name));
        return bandOpt.orElseThrow(() -> new ResourceNotFoundException(
                "No existe banda alguna con el nombre de "+name));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Set<Band> getAllBandsSet() {
        final Set<Band> bandsSet = new HashSet<Band>();
        bandsSet.addAll(this.bandRepository.findAll());// converts List to Set and returns it
        return bandsSet;
    }

    /**
     * This method is used to get all bands where user who is authenticated is the director.
     * @param director is the nickname of the user who created or else manages the obtained bands
     * @param loggedInUserNickname is the nickname of the user who is authenticated and who makes
     * the request. */
    public Set<Band> getBandsSetByDirectorAndLoggedInUserNicknames(
        String director,
        String loggedInUserNickname
    ) {
        final Set<Band> bands = this.bandRepository
                .findByDirector(director)
                .stream()
                .filter(b -> b
                        .getUsers()
                        .stream()
                        .anyMatch(u -> u.getNickname().equals(loggedInUserNickname)))
                .collect(Collectors.toSet());
        return bands;
    }

    /**
     * This method retrieves all bands, as a Set, where a particular user and the user who
     * is authenticated are members.
     * @param nickname related member user nickname to retrieve bands
     * @param loggedInUserNickname is the nickname of the user who is authenticated and who
     * makes the request*/
    public Set<Band> getBandsSetByMemberUserAndLoggedInUserNicknames(
        String nickname,
        String loggedInUserNickname
    ) {
        List<Band> bands = this.bandRepository.findAll();
        return bands
            .stream()
            .filter(b -> b
                    .getUsers()
                    .stream()
                    .anyMatch(user -> user.getNickname().equals(nickname)) 
                            && b.getUsers()
                                .stream()
                                .anyMatch(otherUser -> otherUser.getNickname().equals(loggedInUserNickname)))
            .collect(Collectors.toSet());
    }

    /**
     * This method is used to save a new band and it is only 
     * authorized for platform registered users.
     * @param band Band to be saved
     * @return Band saved
     */
    public Band saveBand(Band band, String director) {
        band.setDirector(director);   // to set user who is authenticated and will create this band
        return this.bandRepository.save(band);
    }

    @PreAuthorize("#band.director == authentication.name or hasRole('ADMIN')")
    public void deleteBand(@Param("band") Band band) {
        this.bandRepository.delete(band);
    }

    /**
     * @param bandId unique id number of band to update
     * @param band entity to update
     * @param loggedInUserNickname user who is authenticated and who makes the request
     */
    public Band updateBand(Short bandId, Band band, String loggedInUserNickname) {
        final Band foundBand = getBandById(bandId);
        // ensure the band to be updated has as director property the authenticated user nickname value
        if (!loggedInUserNickname.equals(foundBand.getDirector()))
            throw new HttpClientErrorException(HttpStatusCode.valueOf(401));
        if (foundBand.getBandId()!=band.getBandId()) {
            throw new IllegalArgumentException(
                "Conflicto entre el argumento id de banda y la propiedad id de banda, no coinciden");
        }
        return this.bandRepository.saveAndFlush(band);
    }

    /**
     * @param bandId unique id number of band wich is related with the event to add
     * @param event entity to add to band
     * @param loggedInUserNickname user who is authenticated and who makes the request
     */
    public Band addEventToBand(short bandId, Event event, String loggedInUserNickname) {
        final Band foundBand = getBandById(bandId);
        if (!foundBand.getDirector().equals(loggedInUserNickname))
            throw new HttpClientErrorException(HttpStatusCode.valueOf(401));
        if (foundBand
                .getEvents()
                .stream()
                .anyMatch(e -> e.getEventId().equals(event.getEventId()))) {// check if event already exists
                    throw new IllegalArgumentException(
                        "Ya existe un evento con el mismo nombre, por favor modifíquelo");
        }
        List<Event> events = foundBand.getEvents();
        events.add(event);
        foundBand.setEvents(events);
        return this.bandRepository.saveAndFlush(foundBand);
    }

    /**
     * @param bandId unique id number of band wich is related with the event to add
     * @param user entity to add to band
     * @param authUsername user who is authenticated and who makes the request
     */
    public Band addMemberUserToBand(short bandId, User user, String authUsername) {
        final Band foundBand = getBandById(bandId);
        if (!authUsername.equals(foundBand.getDirector()))
            throw new HttpClientErrorException(HttpStatusCode.valueOf(401));
        final List<User> bandUsers = foundBand.getUsers();
        if (foundBand
                .getUsers()
                .stream()
                .noneMatch(u ->  u.getUserId()==user.getUserId())) {// check the user is not a member of the band
                    bandUsers.add(user);
                    foundBand.setUsers(bandUsers);
                    return this.bandRepository.saveAndFlush(foundBand);
        }
        return null;
    }
}
