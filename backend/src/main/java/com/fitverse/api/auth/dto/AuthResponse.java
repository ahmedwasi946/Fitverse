package com.fitverse.api.auth.dto;

import com.fitverse.api.user.dto.UserResponse;

public record AuthResponse(
        String token,
        String tokenType,
        UserResponse user
) {
    public static AuthResponse of(String token, UserResponse user) {
        return new AuthResponse(token, "Bearer", user);
    }
}
