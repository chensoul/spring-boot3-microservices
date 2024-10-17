package com.chensoul.ecommerce.payment;

import org.springframework.stereotype.Service;

@Service
public class PaymentMapper {

  public Payment toPayment(PaymentRequest request) {
    if (request == null) {
      return null;
    }
    return Payment.builder()
            .orderId(request.orderId())
            .paymentMethod(request.paymentMethod())
            .amount(request.amount())
            .build();
  }
}
