package com.fitverse.api.ai.dto;

import jakarta.validation.constraints.NotBlank;

/** Placeholder request shape for the AI Avatar feature — see {@link com.fitverse.api.ai.AiService}. */
public record AvatarRequest(

        @NotBlank(message = "A photo is required")
        String photoBase64
) {
}
