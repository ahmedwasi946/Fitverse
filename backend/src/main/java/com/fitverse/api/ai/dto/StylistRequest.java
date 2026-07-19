package com.fitverse.api.ai.dto;

import java.util.List;

/** Placeholder request shape for the AI Stylist — see {@link com.fitverse.api.ai.AiService}. */
public record StylistRequest(
        String occasion,
        List<String> styles,
        String budget
) {
}
