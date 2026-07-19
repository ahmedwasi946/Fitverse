package com.fitverse.api.ai.dto;

import java.util.List;

/**
 * Shared placeholder response for every "AI suggests some products" endpoint
 * (AI Stylist, Smart Recommendations, Outfit Suggestions) — see
 * {@link com.fitverse.api.ai.AiService}.
 */
public record AiSuggestionResponse(
        String message,
        List<Long> productIds
) {
}
