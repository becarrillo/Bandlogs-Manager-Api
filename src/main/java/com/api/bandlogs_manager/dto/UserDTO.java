package com.api.bandlogs_manager.dto;

import com.api.bandlogs_manager.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Project: bandlogs-manager
 * Author: Brando Eli Carrillo Perez
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class UserDTO {
    private String nickname;
    private String password;
}
