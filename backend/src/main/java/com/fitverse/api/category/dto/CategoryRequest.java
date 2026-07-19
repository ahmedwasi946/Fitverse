package com.fitverse.api.category.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequest(

        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Slug is required")
        @jakarta.validation.constraints.Pattern(regexp = "^[a-z0-9-]+$", message = "Slug may only contain lowercase letters, numbers and hyphens")
        String slug,

        String description
) {
}
