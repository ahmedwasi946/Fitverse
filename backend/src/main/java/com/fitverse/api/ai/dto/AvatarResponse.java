package com.fitverse.api.ai.dto;

/** Placeholder response shape for the AI Avatar feature — see {@link com.fitverse.api.ai.AiService}. */
public record AvatarResponse(
        String message,
        String avatarUrl
) {
}
