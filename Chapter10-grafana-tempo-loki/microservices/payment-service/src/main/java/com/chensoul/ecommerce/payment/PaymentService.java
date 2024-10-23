package com.chensoul.ecommerce.payment;

import com.chensoul.ecommerce.product.NotificationProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

  private final PaymentRepository repository;
  private final PaymentMapper mapper;
  private final NotificationProducer notificationProducer;

  public Integer createPayment(PaymentRequest request) {
    Payment payment = this.repository.save(this.mapper.toPayment(request));

    this.notificationProducer.sendNotification(
            new PaymentConfirmation(
                    payment.getId(),
                    payment.getOrderId(),
                    payment.getAmount(),
                    payment.getPaymentMethod(),
                    request.customerResponse().firstname(),
                    request.customerResponse().lastname(),
                    request.customerResponse().email()
            )
    );

    return payment.getId();
  }
}
