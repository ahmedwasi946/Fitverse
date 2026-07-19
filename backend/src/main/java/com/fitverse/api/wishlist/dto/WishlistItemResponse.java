package com.fitverse.api.wishlist.dto;

import java.math.BigDecimal;

public record WishlistItemResponse(
        Long productId,
        String name,
        String brand,
        String imageUrl,
        BigDecimal price,
        BigDecimal salePrice
) {
}
