package com.api.bandlogs_manager.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Project: bandlogs-manager
 * Author: Brando Eli Carrillo Perez
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class LoginUserDTO {
    private String nickname;
    private String password;
}
