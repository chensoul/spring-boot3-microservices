package com.chensoul.ecommerce.product;

import com.chensoul.ecommerce.order.OrderConfirmation;

/**
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 * @since
 */
public interface OrderProducer {
    void sendNotification(OrderConfirmation orderConfirmation);
}
