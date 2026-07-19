package com.fitverse.api.user;

/**
 * Authorities granted to a user. Spring Security expects role names to be
 * usable directly as {@code ROLE_*} authorities.
 */
public enum Role {
    ROLE_USER,
    ROLE_ADMIN
}
