package com.api.bandlogs_manager.external.services;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;


/**
 * Project: bandlogs-manager
 * Author: Brando Eli Carrillo Perez
 */
@Service
public class WhatsAppNotificationMessagingService {
    public final OkHttpClient okHttpClient;

    public WhatsAppNotificationMessagingService(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    @Value("${rapidapi.key}")
    private String rapidApiKey;

    public void sendMessageAsString(String phoneNumber, String message) throws IOException {
        final MediaType mediaType = MediaType.parse("application/json");

        // Construct the message payload
        final String payload = "{\"phone_number_or_group_id\":\""
                .concat(phoneNumber.replace("+", "")) // Ensure WhatsApp phone number does not have '+' country code preffix
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
                System.out.print("Failed to send WhatsApp message: " + response.body().string());
            } else {
                System.out.print("WhatsApp message sent successfully!");
            }
        }
    }
}
