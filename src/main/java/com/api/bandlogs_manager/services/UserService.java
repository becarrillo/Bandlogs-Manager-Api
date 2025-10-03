package com.api.bandlogs_manager.services;

import com.api.bandlogs_manager.dtos.UserRoleDTO;

import com.api.bandlogs_manager.entities.User;

import com.api.bandlogs_manager.enums.UserRole;

import com.api.bandlogs_manager.exceptions.ResourceNotFoundException;

import com.api.bandlogs_manager.repository.UserRepository;

import java.io.IOException;

import java.util.List;
import java.util.Optional;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.data.repository.query.Param;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;


/**
 * Project: bandlogs-manager
 * Author: Brando Eli Carrillo Perez
 */
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public final OkHttpClient okHttpClient;
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Value("${rapidapi.key}")
    private String rapidApiKey;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, OkHttpClient okHttpClient) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.okHttpClient = okHttpClient;
    }

    public User getUserById(Integer id) {
        Optional<User> userOpt = this.userRepository.findById(id);
        return userOpt.orElseThrow(ResourceNotFoundException::new);
    }

    public User getUserByPhoneNumber(String phoneNumber) {
        final User foundUser = this.userRepository.findByPhoneNumber(phoneNumber);
        return foundUser;
    }

    public User getUserByNickname(String nickname) {
        final User foundUser = this.userRepository.findByNickname(nickname);
        return foundUser;
    }

    public List<User> getAllUsers() {
        return this.userRepository.findAll();
    }

    public User registerUser(User user)  throws IOException {
        user.setPassword(this.passwordEncoder.encode(user.getPassword()));
        user.setRole(UserRole.ROLE_USER); // for secure purposes we must not allow to any user to be admin by himself
        return this.userRepository.save(user);
    }

    public void sendWhatsAppMessage(String phoneNumber, String userFirstname) throws IOException {
        final MediaType mediaType = MediaType.parse("application/json");
        String message = "Hola ";
        message +=  userFirstname;
        message +=  " 👋, soy Brando Carrillo. Te doy la bienvenida a mi web app Bandlogs Manager! 📯📲💻  ";
        message += "Tu registro fue exitoso!✔🎉  Espero, la plataforma te ayude a gestionar tus grupos, eventos musicales y su repertorio ";
        message += "🎼 de una manera intuitiva!  Ingresa y aprovecha todo su potencial.  Solicitudes, soporte o dudas por este medio.";
        message += " (Mensaje autogenerado) ";

        // Construct the message payload
        final String payload = "{\"phone_number_or_group_id\":\""
                .concat(phoneNumber.replace("+", ""))   // Ensure WhatsApp phone number does not have '+' country code preffix
                // because WhatsApp number value must be without this character for My Whinlite (external) Api, but it requires county code 
                .concat("\",\"message\":\"")
                .concat(message).concat("\",\"is_group\":false}");
        RequestBody body = RequestBody.create(payload, mediaType);
        Request request = new Request.Builder()
                .url("https://mywhinlite.p.rapidapi.com/sendmsg")
                .post(body)
                .addHeader("x-rapidapi-key", rapidApiKey)
                .addHeader("x-rapidapi-host", "mywhinlite.p.rapidapi.com")
                .addHeader("Content-Type", "application/json")
                .build();
        try (Response response = this.okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error("Failed to send WhatsApp message: " + response.body().string());
            } else {
                log.info("WhatsApp message sent successfully!");
            }
        }
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    public User setUserRoleByUserId(Integer userId, UserRoleDTO dto) {
        // This method is a placeholder for setting the user role based on the access token.
        // The actual implementation would depend on the authentication and authorization logic.
        final Optional<User> userOpt = this.userRepository.findById(userId);
        if (userOpt.isPresent()) {
            if (!userOpt.get().getNickname().equals(dto.nickname)) {
                throw new IllegalArgumentException(
                    "El nickname esperado en el usuario recuperado no concuerda con el del cuerpo de petición (tipo UserRoleDTO)");
            }
            User user = userOpt.get();
            user.setRole(dto.role);
            return this.userRepository.saveAndFlush(user);
        }
        return null;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(User user) {
        this.userRepository.delete(user);
    }

    @PreAuthorize("#u.nickname == authentication.name or hasRole('ADMIN')")
    public User updateUser(@Param("u") User olduser, User newuser) {
        if (!newuser.getPassword().equals(olduser.getPassword()) &&
            !this.passwordEncoder.matches(newuser.getPassword(), olduser.getPassword())) {
                newuser.setPassword(this.passwordEncoder.encode(newuser.getPassword()));
        } else {
            newuser.setPassword(olduser.getPassword());
        }
        return this.userRepository.saveAndFlush(newuser);
    }
}