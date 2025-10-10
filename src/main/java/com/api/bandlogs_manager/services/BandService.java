package com.api.bandlogs_manager.services;

import com.api.bandlogs_manager.entities.Band;
import com.api.bandlogs_manager.entities.Event;
import com.api.bandlogs_manager.entities.Song;
import com.api.bandlogs_manager.entities.User;

import com.api.bandlogs_manager.exceptions.ResourceNotFoundException;

import com.api.bandlogs_manager.repository.BandRepository;
import com.api.bandlogs_manager.repository.UserRepository; // Import UserRepository
import com.api.bandlogs_manager.repository.SongRepository;

import jakarta.transaction.Transactional;

import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatusCode;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Project: bandlogs-manager
 * Author: Brando Eli Carrillo Perez
 */
@Service
public class BandService {
    private final BandRepository bandRepository;
    private final UserRepository userRepository; // Inject UserRepository
    private final SongRepository songRepository;

    public BandService(BandRepository bandRepository, UserRepository userRepository, SongRepository songRepository) {
        this.bandRepository = bandRepository;
        this.userRepository = userRepository;
        this.songRepository = songRepository;
    }

    public Band getBandById(Short id) {
        Optional<Band> bandOpt = this.bandRepository.findById(id);
        return bandOpt.orElseThrow(ResourceNotFoundException::new);
    }

    public List<Band> listBandsByNameContaining(String containing) {
        final Optional<List<Band>> bandsOpt = Optional.ofNullable(this.bandRepository.findByNameContaining(containing));
        return bandsOpt.orElseThrow(() -> new ResourceNotFoundException(
                "No existe banda alguna con el nombre que contiene: " + containing));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Set<Band> getAllBandsSet() {
        final Set<Band> bandsSet = new HashSet<>();
        bandsSet.addAll(this.bandRepository.findAll());// converts List to Set and returns it
        return bandsSet;
    }

    /**
     * This method is used to get all bands where user who is authenticated is the
     * director.
     * 
     * @param director             is the nickname of the user who created or else
     *                             manages the obtained bands
     * @param loggedInUserNickname is the nickname of the user who is authenticated
     *                             and who makes
     *                             the request.
     */
    public Set<Band> getBandsSetByDirectorAndLoggedInUserNicknames(
            String director,
            String loggedInUserNickname) {
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
     * This method retrieves all bands, as a Set, where a particular user and the
     * user who
     * is authenticated are members.
     * 
     * @param nickname             related member user nickname to retrieve bands
     * @param loggedInUserNickname is the nickname of the user who is authenticated
     *                             and who
     *                             makes the request
     */
    public Set<Band> getBandsSetByLoggedInMemberUserNickname(String loggedInUserNickname) {
        final List<Band> bandsList = this.bandRepository.findAll();
        return bandsList.stream()
                .filter(band -> band.getUsers()
                        .stream()
                        .anyMatch(user -> user.getNickname().equals(loggedInUserNickname)))
                .collect(Collectors.toSet());
    }

    /**
     * This method is used to save a new band and it is only
     * authorized for platform registered users.
     * 
     * @param band Band to be saved
     * @param director nickname of user registering the new band (default he/she will be a director)
     * @return Band saved
     */
    public Band saveBand(Band band, String director) {
        band.setDirector(director); // to set user who is authenticated and will create this band
        return this.bandRepository.save(band);
    }

    @PreAuthorize("#band.director == authentication.name or hasRole('ADMIN')")
    public void deleteBand(@Param("band") Band band) {
        this.bandRepository.delete(band);
    }

    /**
     * @param bandId               unique id number of band to update
     * @param bandDetails          Band entity to update
     * @param loggedInUserNickname user who is authenticated and who makes the
     *                             request
    */ 
    @Transactional // Add transactional annotation
    public Band updateBand(Short bandId, Band bandDetails, String loggedInUserNickname) {
        Band band = bandRepository.findById(bandId)
                .orElseThrow(() -> new ResourceNotFoundException("Band not found with id: " + bandId));
        if (!loggedInUserNickname.equals(band.getDirector()))
            throw new HttpClientErrorException(HttpStatusCode.valueOf(403));
        // Update simple attributes
        band.setName(bandDetails.getName());
        band.setMusicalGenre(bandDetails.getMusicalGenre());
        band.setDirector(bandDetails.getDirector());

        if (bandDetails.getUsers()!=null && bandDetails.getUsers().size()==band.getUsers().size()-1)
            return bandRepository.saveAndFlush(bandDetails); // For the operation to cancel a band member
        if (bandDetails.getUsers() != null) {   // Handle user associations
            for (User newUser : bandDetails.getUsers()) {
                // Check if the user already exists in the band
                boolean alreadyAssociated = band.getUsers().stream()
                        .anyMatch(user -> user.getUserId()==newUser.getUserId()); // Assuming User has a getId() method

                if (!alreadyAssociated) {
                    //Fetch the user from the database to avoid detached entity issues
                    User managedUser = userRepository
                        .findById(newUser.getUserId())
                        .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + newUser.getUserId()));
                    band.getUsers().add(managedUser);
                }
            }
        }

        // Save and flush updated band changes
        return bandRepository.saveAndFlush(band);
    }

    /**
     * Add or update band inner event
     * @param bandId               unique id number of band wich is related with the
     *                             event to add
     * @param event                entity to add to band
     * @param loggedInUserNickname user who is authenticated and who makes the
     *                             request
     */
    public Band patchEventToBand(short bandId, Event event, String loggedInUserNickname) {
        final Band foundBand = getBandById(bandId);
        if (!foundBand.getDirector().equals(loggedInUserNickname))
            throw new HttpClientErrorException(HttpStatusCode.valueOf(403));
        final List<Event> events = foundBand.getEvents();
        final Set<Song> repertoire = new HashSet<>();
        event.getRepertoire()
                        .forEach(s -> {
                            repertoire.add(songRepository.save(s));
                        });
        event.setRepertoire(repertoire);
        events.add(event);
        foundBand.setEvents(events);
        return this.bandRepository.save(foundBand);
    }

    /**
     * @param bandId       unique id number of band wich is related with the event                  to add
     * @param user         entity to add to band
     * @param authUsername user who is authenticated and who makes the request
     */
    public Band addMemberUserToBand(short bandId, User user, String authUsername) {
        final Band foundBand = getBandById(bandId);
        if (!authUsername.equals(foundBand.getDirector()))
            throw new HttpClientErrorException(HttpStatusCode.valueOf(403));
        final List<User> bandUsers = foundBand.getUsers();
        if (foundBand
                .getUsers()
                .stream()
                .noneMatch(u -> u.getUserId() == user.getUserId())) {// check the user is not a member of the band
            bandUsers.add(user);
            foundBand.setUsers(bandUsers);
            return this.bandRepository.saveAndFlush(foundBand);
        }
        return null;
    }

    /**
     * It adds the user to related Bands before some changes
     * @param oldUser the User entity before some changes
     * @param user         entity to add to band
     */
    public void addMemberUserToManyBands(User oldUser, User user, Set<Band> bands) {
        for (Band band : bands) {
            if (bands.stream().anyMatch(b -> b.getUsers().stream().anyMatch(u -> u.getUserId()==user.getUserId()))) {
                final List<User> bandUsers = band.getUsers();
                bandUsers.remove(oldUser);
                bandUsers.add(user);
                band.setUsers(bandUsers);
                this.bandRepository.saveAndFlush(band);
            }
        }
    }

    /**
     * @param bandId               unique id number of band wich is related with the
     *                             event to add
     * @param event                entity to add to band
     * @param loggedInUserNickname user who is authenticated and who makes the
     *                             request
     */
    public Band removeEventInBand(short bandId, Event event, String loggedInUserNickname) {
        final Band foundBand = getBandById(bandId);
        if (!foundBand.getDirector().equals(loggedInUserNickname))
            throw new HttpClientErrorException(HttpStatusCode.valueOf(403));
        List<Event> events = foundBand.getEvents();
        events = events.stream()
                        .filter(e -> !e.getEventId().equals(event.getEventId()))
                        .collect(Collectors.toList());
        foundBand.setEvents(events);
        return this.bandRepository.saveAndFlush(foundBand);
    }

    /**
     * @param bandId               unique id number of band wich is related with the
     *                             event to add
     * @param event                entity to add to band
     * @param loggedInUserNickname user who is authenticated and who makes the
     *                             request
     */
    public Band updateEventInBand(short bandId, Event event, String loggedInUserNickname) {
        final Band foundBand = getBandById(bandId);
        if (!foundBand.getDirector().equals(loggedInUserNickname))
            throw new HttpClientErrorException(HttpStatusCode.valueOf(403));
        event.getRepertoire().forEach(s -> {
            songRepository.saveAndFlush(s);
        });
        List<Event> events = foundBand.getEvents();
        events = events.stream()
                        .map(e -> {
                            if (e.getEventId().equals(event.getEventId()))
                                return event;
                            return e;
                        })
                        .collect(Collectors.toList());
        foundBand.setEvents(events);
        return this.bandRepository.saveAndFlush(foundBand);
    }
}
