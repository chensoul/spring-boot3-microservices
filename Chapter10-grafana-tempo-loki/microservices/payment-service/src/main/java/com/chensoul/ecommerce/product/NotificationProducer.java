package com.chensoul.ecommerce.product;

import com.chensoul.ecommerce.payment.PaymentConfirmation;

/**
 * TODO Comment
 *
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 * @since TODO
 */
public interface NotificationProducer {
  void sendNotification(PaymentConfirmation request);
}
