package com.fitverse.api.ai.dto;

/** Placeholder response shape for Virtual Try-On — see {@link com.fitverse.api.ai.AiService}. */
public record TryOnResponse(
        String message,
        String previewUrl
) {
}
