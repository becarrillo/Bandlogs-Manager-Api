package com.api.bandlogs_manager.controllers;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.api.bandlogs_manager.entities.Band;
import com.api.bandlogs_manager.entities.User;

import com.api.bandlogs_manager.enums.UserRole;
import com.api.bandlogs_manager.exceptions.ResourceNotFoundException;
import com.api.bandlogs_manager.external.services.WhatsAppNotificationMessagingService;
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
    private final WhatsAppNotificationMessagingService whatsAppNotificationMessagingService;

    public UserController(UserService userService,
                        BandService bandService,
                        JwtUtil jwtUtil,
                        WhatsAppNotificationMessagingService whatsAppNotificationMessagingService) {
        this.userService = userService;
        this.bandService = bandService;
        this.jwtUtil = jwtUtil;
        this.whatsAppNotificationMessagingService = whatsAppNotificationMessagingService;
    }

    @GetMapping(path = "/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable("userId") int id) {
        try {
            final User foundUser = this.userService.getUserById(id);
            return new ResponseEntity<>(foundUser, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            throw e;
        }
        
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
                    this.userService.listAllUsers(),
                    HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping(params = {"nombre-de-usuario"})
    public ResponseEntity<List<User>> lisUsersByNicknameContaining(@RequestParam("nombre-de-usuario") String containing) {
        try {
            return new ResponseEntity<>(this.userService.listUsersByNicknameContaining(containing), HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping(path = "/registro")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        try {// Register user and assign result to a variable
            final User savedUser = this.userService.registerUser(user);
            if (Objects.isNull(savedUser))
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            String message = "Hola, ";   // Generate the WhatsApp notification message body
            message +=  savedUser.getFirstname();
            message +=  " 👋, te doy la bienvenida a mi web app Bandlogs Manager en español! 📯📲💻  ";
            message += "Tu registro fue exitoso... ✔🎉 y espero, la plataforma te ayude a gestionar tus grupos, eventos musicales y su repertorio ";
            message += "🎼 de una manera intuitiva.  Ingresa y aprovecha todo su potencial.  Para solicitudes, soporte técnico o dudas por este medio.";
            message += " Brando Carrillo Pérez ~ (@belicarrillo)   ----  Mensaje autogenerado ---- ";
            
            this.whatsAppNotificationMessagingService.sendMessageAsString(
                savedUser.getPhoneNumber().replace("+", ""),// WhatsApp number string, must not have '+' for send msg through external API 
                message
            );
            return new ResponseEntity<>(
                    savedUser,
                    HttpStatus.CREATED);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping(path = "/{userId}/eliminar")
    public ResponseEntity<Void> deleteUser(@PathVariable("userId") int id) {
        try {
            this.userService.deleteUserById(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
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
            if (foundUser.getUserId() != user.getUserId())
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
}
