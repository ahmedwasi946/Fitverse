package com.fitverse.api.user.dto;

import com.fitverse.api.user.Role;

import java.time.Instant;

/**
 * What the API returns for a user. Deliberately excludes
 * {@code passwordHash} — entities are never serialised directly.
 */
public record UserResponse(
        Long id,
        String name,
        String email,
        Role role,
        String avatarUrl,
        Instant createdAt
) {
}
