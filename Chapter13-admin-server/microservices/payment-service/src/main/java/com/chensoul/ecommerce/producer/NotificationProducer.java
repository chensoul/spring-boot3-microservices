package com.chensoul.ecommerce.producer;

import com.chensoul.ecommerce.payment.PaymentConfirmation;

/**
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 * @since
 */
public interface NotificationProducer {
    void sendNotification(PaymentConfirmation request);
}
