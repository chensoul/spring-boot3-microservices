package com.chensoul.ecommerce.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record ProductRequest(
    Integer id,
    @NotBlank(message = "Product name is required")
    String name,
    @NotBlank(message = "Product description is required")
    String description,
    @Positive(message = "Available quantity should be positive")
    Long availableQuantity,
    @Positive(message = "Price should be positive")
    BigDecimal price,
    @NotNull(message = "Product category is required")
    Integer categoryId
) {
}
