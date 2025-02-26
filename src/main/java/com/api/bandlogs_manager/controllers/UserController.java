package com.api.bandlogs_manager.controllers;

import com.api.bandlogs_manager.dto.UserDTO;
import com.api.bandlogs_manager.entities.Band;
import com.api.bandlogs_manager.entities.User;
import com.api.bandlogs_manager.exceptions.ResourceNotFoundException;
import com.api.bandlogs_manager.security.JwtUtil;
import com.api.bandlogs_manager.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Project: bandlogs-manager
 * Author: Brando Eli Carrillo Perez
 */
@RestController
@RequestMapping("/api/v1/usuarios")
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<User> getPersonById(@PathVariable int id) {
        final User foundUser = this.userService.getUserById(id);
        return new ResponseEntity<User>(foundUser, HttpStatus.OK);
    }

    @GetMapping(path = "/usuario", params = {"phone"})
    public ResponseEntity<User> getUserByPhoneNumber(@RequestParam("phone") String phoneNumber) {
        ResponseEntity<User> response = null;

        try {
            final User foundUser = this.userService.getUserByPhoneNumber(phoneNumber);
            if (foundUser !=null)
                response = new ResponseEntity<User>(foundUser, HttpStatus.OK);
        } catch (Exception e) {
            throw new ResourceNotFoundException(e.getMessage());
        }
        return response;
    }

    @GetMapping
    public ResponseEntity<List<User>> listAllUsers() {
        try {
            return new ResponseEntity<List<User>>(
                    this.userService.getAllUsers(),
                    HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping(path = "/{id}/bandas")
    public ResponseEntity<Set<Band>> listBandsByUserId(
            @PathVariable("id") int userId) {

        return new ResponseEntity<Set<Band>>(
                this.userService.getUserById(userId).getBands(),
                HttpStatus.OK);
    }
    // lista las bandas por director director
    @GetMapping(path = "/{id}/bandas/dirigidas")
    public ResponseEntity<Set<Band>> listBandsDirectedByUserWithId(
            @PathVariable("id") int directorUserId) {

        return new ResponseEntity<Set<Band>>(
                this.userService.getUserById(directorUserId)
                        .getBands()
                        .stream()
                        .filter(b ->
                                b.getDirectorUserId()==directorUserId)
                        .collect(Collectors.toSet()),
                HttpStatus.OK);
    }

    @PostMapping(path = "/login")
    public ResponseEntity<String> login(@RequestBody(required = true) Map<String, Object> requestMap) {
        if (userService.userIsAuthenticated(requestMap)) {
            try {
                return new ResponseEntity<String>(jwtUtil.createToken(
                        UserDTO.builder()
                                .nickname((String) requestMap.get("nickname"))
                                .password((String) requestMap.get("password"))
                                .build()),
                        HttpStatus.OK);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return new ResponseEntity<String>("Credenciales incorrectas", HttpStatus.BAD_REQUEST);
    }

    @PostMapping(path = "/agregar")
    public ResponseEntity<User> signUp(@RequestBody User user) {
        final User savedUser = this.userService.signUp(user);
        if (Objects.isNull(savedUser))
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        try {
            return new ResponseEntity<User>(
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
            return new ResponseEntity<Void>(HttpStatus.OK);
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
            return new ResponseEntity<User>(
                    this.userService.updateUser(id, user),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
