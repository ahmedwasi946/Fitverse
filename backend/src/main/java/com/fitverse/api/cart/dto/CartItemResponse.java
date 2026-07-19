package com.fitverse.api.cart.dto;

import java.math.BigDecimal;

public record CartItemResponse(
        Long id,
        Long productId,
        String name,
        String brand,
        String imageUrl,
        String size,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal lineTotal
) {
}
