package com.fitverse.api.ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** Placeholder request shape for Virtual Try-On — see {@link com.fitverse.api.ai.AiService}. */
public record TryOnRequest(

        @NotNull(message = "Product is required")
        Long productId,

        @NotBlank(message = "A photo is required")
        String photoBase64
) {
}
