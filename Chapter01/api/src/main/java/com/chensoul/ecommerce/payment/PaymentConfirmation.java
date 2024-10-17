package com.chensoul.ecommerce.payment;

import java.math.BigDecimal;

public record PaymentConfirmation(
        Integer paymentId,
        Integer orderId,
        BigDecimal amount,
        PaymentMethod paymentMethod,
        String customerFirstname,
        String customerLastname,
        String customerEmail
) {
}
