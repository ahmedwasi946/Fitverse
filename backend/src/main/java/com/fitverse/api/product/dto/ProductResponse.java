package com.fitverse.api.product.dto;

import java.math.BigDecimal;
import java.util.List;

public record ProductResponse(
        Long id,
        String name,
        String brand,
        String description,
        BigDecimal price,
        BigDecimal salePrice,
        Long categoryId,
        List<String> imageUrls,
        List<String> sizes,
        Integer fitConfidence,
        Integer stockQuantity,
        String material,
        String shippingInfo,
        String returnsInfo,
        String careInfo
) {
}
