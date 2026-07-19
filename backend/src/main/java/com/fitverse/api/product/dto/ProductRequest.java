package com.fitverse.api.product.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

public record ProductRequest(

        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Brand is required")
        String brand,

        @NotBlank(message = "Description is required")
        String description,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
        BigDecimal price,

        @DecimalMin(value = "0.0", inclusive = false, message = "Sale price must be greater than 0")
        BigDecimal salePrice,

        @NotNull(message = "Category is required")
        Long categoryId,

        @NotEmpty(message = "At least one image URL is required")
        List<String> imageUrls,

        @NotEmpty(message = "At least one size is required")
        List<String> sizes,

        @Min(value = 0, message = "Fit confidence must be between 0 and 100")
        @Max(value = 100, message = "Fit confidence must be between 0 and 100")
        Integer fitConfidence,

        @NotNull(message = "Stock quantity is required")
        @Min(value = 0, message = "Stock quantity cannot be negative")
        Integer stockQuantity,

        String material,
        String shippingInfo,
        String returnsInfo,
        String careInfo
) {
}
