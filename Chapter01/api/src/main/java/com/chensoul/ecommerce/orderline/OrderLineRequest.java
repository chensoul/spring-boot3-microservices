package com.chensoul.ecommerce.orderline;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record OrderLineRequest(
        Integer id,
        Integer orderId,
        Integer productId,
        Long quantity
) {
}
