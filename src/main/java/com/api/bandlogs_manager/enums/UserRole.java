package com.api.bandlogs_manager.enums;

/**
 * Role to assign to the user details authorities and managing user authorizations by 
 * PreAuthorize and PostAuthorize annotations which could be passed it with "hasRole(${role})"
 * having as $role argument to "USER" or "ADMIN" omitting "ROLE_" prefix by default.
 * Project: bandlogs-manager.
 * Author: Brando Eli Carrillo Perez.
 */
public enum UserRole {
    ROLE_ADMIN,
    ROLE_USER
}
