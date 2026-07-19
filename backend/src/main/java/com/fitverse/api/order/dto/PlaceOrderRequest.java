package com.fitverse.api.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record PlaceOrderRequest(

        @NotNull(message = "Shipping address is required")
        @Valid
        AddressDto shippingAddress
) {
}
