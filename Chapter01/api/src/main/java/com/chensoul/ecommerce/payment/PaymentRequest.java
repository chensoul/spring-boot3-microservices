package com.chensoul.ecommerce.payment;

import com.chensoul.ecommerce.customer.CustomerResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record PaymentRequest(
        Integer id,
        Integer orderId,
        BigDecimal amount,
        PaymentMethod paymentMethod,
        CustomerResponse customerResponse
) {
}
