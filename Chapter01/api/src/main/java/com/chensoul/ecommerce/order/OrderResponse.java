package com.chensoul.ecommerce.order;

import com.chensoul.ecommerce.payment.PaymentMethod;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.math.BigDecimal;

@JsonInclude(Include.NON_EMPTY)
public record OrderResponse(
    Integer id,
    BigDecimal amount,
    PaymentMethod paymentMethod,
    String customerId
) {

}
