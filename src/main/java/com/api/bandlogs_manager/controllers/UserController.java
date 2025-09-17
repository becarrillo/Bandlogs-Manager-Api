package com.api.bandlogs_manager.controllers;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
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

import com.api.bandlogs_manager.dtos.UserRoleDTO;

import com.api.bandlogs_manager.entities.Band;
import com.api.bandlogs_manager.entities.User;

import com.api.bandlogs_manager.enums.UserRole;
import com.api.bandlogs_manager.exceptions.ResourceNotFoundException;
import com.api.bandlogs_manager.security.JwtUtil;

import com.api.bandlogs_manager.services.BandService;
import com.api.bandlogs_manager.services.UserService;

import io.jsonwebtoken.Claims;



/**
 * Project: bandlogs-manager
 * Author: Brando Eli Carrillo Perez
 */
@RestController
@RequestMapping("/api/v1/usuarios")
public class UserController {
    private final UserService userService;
    private final BandService bandService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, BandService bandService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.bandService = bandService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping(path = "/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable("userId") int id) {
        final User foundUser = this.userService.getUserById(id);
        return new ResponseEntity<>(foundUser, HttpStatus.OK);
    }

    @GetMapping(path = "/usuario", params = {"nombre-de-usuario"})
    public ResponseEntity<User> getUserByNickname(@RequestParam("nombre-de-usuario") String nickname) {
        ResponseEntity<User> response = null;
        try {
            final User foundUser = this.userService.getUserByNickname(nickname);
            response = new ResponseEntity<>(foundUser, HttpStatus.OK);
        } catch (Exception e) {
            throw new ResourceNotFoundException(e.getMessage());
        }
        return response;
    }

    @GetMapping(path = "/usuario", params = {"telefono"})
    public ResponseEntity<User> getUserByPhoneNumber(@RequestParam("telefono") String phoneNumber) {
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

    @PostMapping(path = "/registro")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        final User savedUser = this.userService.registerUser(user);
        if (Objects.isNull(savedUser))
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        try {
            return new ResponseEntity<>(
                    savedUser,
                    HttpStatus.CREATED);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping(path = "/eliminar")
    public ResponseEntity<Void> deleteUser(@RequestBody User user) {
        try {
            this.userService.deleteUser(user);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PutMapping(path = "/{userId}/modificar")
    public ResponseEntity<User> updateUser(
        @RequestHeader("Authorization") String authHeader,
        @PathVariable("userId") int id,
        @RequestBody User user) 
    {
        try {
            final String token = authHeader.replace("Bearer ", "");
            final Claims claims = this.jwtUtil.extractAllClaims(token);
            final User foundUser = this.userService.getUserById(id);
            if (Optional.of(foundUser).isPresent() && foundUser.getUserId() != user.getUserId())
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            if (foundUser.getUserId() == claims.get("id", Integer.class) 
                && !foundUser.getRole().equals(UserRole.valueOf(claims.get("role", String.class)))
                && user.getRole().equals(UserRole.ROLE_ADMIN))// when user tries to set his role to admin without authorization
                    user.setRole(UserRole.ROLE_USER);
            final String authUsername = this.jwtUtil.extractUsername(token);
            Set<Band> bands = this.bandService.getBandsSetByLoggedInMemberUserNickname(authUsername);
            bands = bands.stream()
                    .filter(b -> b.getUsers().contains(foundUser))
                    .collect(Collectors.toSet());

            final User updatedUser = this.userService.updateUser(foundUser, user);
            this.bandService.addMemberUserToManyBands(foundUser, updatedUser, bands);
            return new ResponseEntity<>(
                    updatedUser,
                    HttpStatus.OK
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PatchMapping(path = "/{userId}/rol/modificar")
    public ResponseEntity<User> patchUserRoleByUserId(
        @PathVariable("userId") int id, @RequestBody UserRoleDTO dto) {
            User roledUser;
            roledUser = this.userService.setUserRoleByUserId(id, dto);
            if (roledUser == null) {
                throw new ResourceNotFoundException("Usuario no encontrado");
            }
            return new ResponseEntity<>(
                roledUser,
                HttpStatus.OK);
    }
}
