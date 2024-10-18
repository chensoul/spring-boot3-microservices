package com.chensoul.ecommerce.product;

import java.math.BigDecimal;

public record ProductResponse(
    Integer id,
    String name,
    String description,
    Long availableQuantity,
    BigDecimal price,
    Integer categoryId,
    String categoryName,
    String categoryDescription
) {
}
