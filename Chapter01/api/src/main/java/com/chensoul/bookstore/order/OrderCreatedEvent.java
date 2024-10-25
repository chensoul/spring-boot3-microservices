package com.chensoul.bookstore.order;

import com.chensoul.bookstore.event.EventAware;
import java.time.LocalDateTime;
import java.util.Set;

public record OrderCreatedEvent(
        String eventId,
        String orderNumber,
        Set<OrderItem> items,
        Customer customer,
        Address deliveryAddress,
        LocalDateTime createdAt)
        implements EventAware {}
