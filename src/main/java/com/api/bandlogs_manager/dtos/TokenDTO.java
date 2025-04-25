package com.api.bandlogs_manager.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Project: bandlogs-manager
 * Author: Brando Eli Carrillo Perez
 */
@NoArgsConstructor
@Getter
public class TokenDTO {
    private String accessToken;

    public TokenDTO(String accessToken) {
        this.accessToken = accessToken;
    }
}