package com.api.bandlogs_manager.dtos;

/**
 * Project: bandlogs-manager
 * Author: Brando Eli Carrillo Perez
 */
public class TokenDTO {
    public String accessToken;

    public TokenDTO() {
    }

    public TokenDTO(String accessToken) {
        this.accessToken = accessToken;
    }
}