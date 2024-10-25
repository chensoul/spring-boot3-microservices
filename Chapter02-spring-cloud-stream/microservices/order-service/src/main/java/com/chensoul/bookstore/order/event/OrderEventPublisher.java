package com.chensoul.bookstore.order.event;

import com.chensoul.bookstore.event.Event;
import com.chensoul.bookstore.event.EventAware;
import com.chensoul.bookstore.order.OrderCancelledEvent;
import com.chensoul.bookstore.order.OrderCreatedEvent;
import com.chensoul.bookstore.order.OrderDeliveredEvent;
import com.chensoul.bookstore.order.OrderErrorEvent;
import com.chensoul.bookstore.order.OrderEventType;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class OrderEventPublisher {
    private final StreamBridge streamBridge;

    OrderEventPublisher(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    public void publish(OrderCreatedEvent event) {
        this.send(OrderEventType.ORDER_CREATED, event);
    }

    public void publish(OrderDeliveredEvent event) {
        this.send(OrderEventType.ORDER_DELIVERED, event);
    }

    public void publish(OrderCancelledEvent event) {
        this.send(OrderEventType.ORDER_CANCELLED, event);
    }

    public void publish(OrderErrorEvent event) {
        this.send(OrderEventType.ORDER_PROCESSING_FAILED, event);
    }

    private void send(OrderEventType orderEventType, EventAware payload) {
        Message message = MessageBuilder.withPayload(new Event(orderEventType, payload.eventId(), payload))
                .setHeader("partitionKey", payload.eventId())
                .build();
        streamBridge.send("notification-out-0", message);
    }
}
