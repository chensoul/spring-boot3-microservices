package com.chensoul.ecommerce.producer;

import com.chensoul.ecommerce.order.OrderConfirmation;

/**
 * TODO Comment
 *
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 * @since TODO
 */
public interface OrderProducer {
    void sendNotification(OrderConfirmation orderConfirmation);
}
