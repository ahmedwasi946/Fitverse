package com.fitverse.api.order.dto;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long productId,
        String productName,
        String imageUrl,
        String size,
        Integer quantity,
        BigDecimal unitPrice
) {
}
