package com.api.bandlogs_manager.controllers;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.api.bandlogs_manager.entities.Band;
import com.api.bandlogs_manager.entities.User;
import com.api.bandlogs_manager.exceptions.ResourceNotFoundException;
import com.api.bandlogs_manager.services.UserService;

/**
 * Project: bandlogs-manager
 * Author: Brando Eli Carrillo Perez
 */
@RestController
@RequestMapping("/api/v1/usuarios")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<User> getPersonById(@PathVariable int id) {
        final User foundUser = this.userService.getUserById(id);
        return new ResponseEntity<>(foundUser, HttpStatus.OK);
    }

    @GetMapping(path = "/usuario", params = {"phone"})
    public ResponseEntity<User> getUserByPhoneNumber(@RequestParam("phone") String phoneNumber) {
        ResponseEntity<User> response = null;

        try {
            final User foundUser = this.userService.getUserByPhoneNumber(phoneNumber);
            if (foundUser !=null)
                response = new ResponseEntity<>(foundUser, HttpStatus.OK);
        } catch (Exception e) {
            throw new ResourceNotFoundException(e.getMessage());
        }
        return response;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<User>> listAllUsers() {
        try {
            return new ResponseEntity<>(
                    this.userService.getAllUsers(),
                    HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping(path = "/{id}/bandas")
    public ResponseEntity<Set<Band>> listBandsByUserId(
            @PathVariable("id") int userId) {

        return new ResponseEntity<>(
                this.userService.getUserById(userId).getBands(),
                HttpStatus.OK);
    }
    // lista las bandas por director
    @GetMapping(path = "/{id}/bandas/dirigidas")
    public ResponseEntity<Set<Band>> listBandsDirectedByUserWithId(
            @PathVariable("id") int directorUserId) {

        return new ResponseEntity<>(
                this.userService.getUserById(directorUserId)
                        .getBands()
                        .stream()
                        .filter(b ->
                                b.getDirectorUserId()==directorUserId)
                        .collect(Collectors.toSet()),
                HttpStatus.OK);
    }

    @DeleteMapping(path = "/{userId}/eliminar")
    public ResponseEntity<Void> deleteUser(@PathVariable("userId") int id) {
        try {
            this.userService.deleteUserById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PutMapping(path = "/{userId}/modificar")
    public ResponseEntity<User> updateUser(@PathVariable("userId") int id, @RequestBody User user) {
        final User foundUser = this.userService.getUserById(id);
        if (foundUser.getUserId()!= user.getUserId()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            return new ResponseEntity<>(
                    this.userService.updateUser(id, user),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
