package com.fitverse.api.order.dto;

import com.fitverse.api.order.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
        Long id,
        String orderNumber,
        List<OrderItemResponse> items,
        AddressDto shippingAddress,
        BigDecimal subtotal,
        BigDecimal shipping,
        BigDecimal total,
        OrderStatus status,
        Instant createdAt
) {
}
