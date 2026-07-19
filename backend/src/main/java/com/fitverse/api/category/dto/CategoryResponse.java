package com.fitverse.api.category.dto;

public record CategoryResponse(
        Long id,
        String name,
        String slug,
        String description
) {
}
