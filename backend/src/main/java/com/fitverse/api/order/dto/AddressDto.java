package com.fitverse.api.order.dto;

import jakarta.validation.constraints.NotBlank;

public record AddressDto(

        @NotBlank(message = "Full name is required")
        String fullName,

        @NotBlank(message = "Address is required")
        String line1,

        @NotBlank(message = "City is required")
        String city,

        @NotBlank(message = "ZIP code is required")
        String zip,

        @NotBlank(message = "Country is required")
        String country
) {
}
