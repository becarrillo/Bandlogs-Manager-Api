package com.api.bandlogs_manager.dtos;

import com.api.bandlogs_manager.enums.UserRole;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRoleDTO {
    public String nickname;
    public UserRole role;
}