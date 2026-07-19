package com.fitverse.api.order.dto;

import com.fitverse.api.order.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record OrderStatusUpdateRequest(

        @NotNull(message = "Status is required")
        OrderStatus status
) {
}
