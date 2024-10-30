package com.chensoul.bookstore.notification.event;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import com.chensoul.bookstore.event.Event;
import com.chensoul.bookstore.notification.AbstractIT;
import com.chensoul.bookstore.notification.ApplicationProperties;
import com.chensoul.bookstore.order.Address;
import com.chensoul.bookstore.order.Customer;
import com.chensoul.bookstore.order.OrderCancelledEvent;
import com.chensoul.bookstore.order.OrderCreatedEvent;
import com.chensoul.bookstore.order.OrderDeliveredEvent;
import com.chensoul.bookstore.order.OrderErrorEvent;
import com.chensoul.bookstore.order.OrderEventType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

class OrderEventHandlerTests extends AbstractIT {
    @Autowired
    StreamBridge streamBridge;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ApplicationProperties properties;

    Customer customer = new Customer("Chensoul", "ichensoul@gmail.com", "999999999");
    Address address = new Address("addr line 1", null, "Hyderabad", "TS", "500072", "India");

    @Test
    void shouldHandleOrderCreatedEvent() throws JsonProcessingException {
        String orderNumber = UUID.randomUUID().toString();

        OrderCreatedEvent payload = new OrderCreatedEvent(
                UUID.randomUUID().toString(), orderNumber, Set.of(), customer, address, LocalDateTime.now());

        Message message = MessageBuilder.withPayload(
                        new Event(OrderEventType.ORDER_CREATED, payload.eventId(), payload))
                .setHeader("partitionKey", payload.eventId())
                .build();

        streamBridge.send("notification-out-0", message);

        await().atMost(30, SECONDS).untilAsserted(() -> {
            verify(notificationService).sendOrderCreatedNotification(any(OrderCreatedEvent.class));
        });
    }

    @Test
    void shouldHandleOrderDeliveredEvent() {
        String orderNumber = UUID.randomUUID().toString();

        var payload = new OrderDeliveredEvent(
                UUID.randomUUID().toString(), orderNumber, Set.of(), customer, address, LocalDateTime.now());

        Message message = MessageBuilder.withPayload(
                        new Event(OrderEventType.ORDER_DELIVERED, payload.eventId(), payload))
                .setHeader("partitionKey", payload.eventId())
                .build();

        streamBridge.send("notification-out-0", message);

        await().atMost(30, SECONDS).untilAsserted(() -> {
            verify(notificationService).sendOrderDeliveredNotification(any(OrderDeliveredEvent.class));
        });
    }

    @Test
    void shouldHandleOrderCancelledEvent() {
        String orderNumber = UUID.randomUUID().toString();

        var payload = new OrderCancelledEvent(
                UUID.randomUUID().toString(),
                orderNumber,
                Set.of(),
                customer,
                address,
                "test cancel reason",
                LocalDateTime.now());

        Message message = MessageBuilder.withPayload(
                        new Event(OrderEventType.ORDER_CANCELLED, payload.eventId(), payload))
                .setHeader("partitionKey", payload.eventId())
                .build();

        streamBridge.send("notification-out-0", message);

        await().atMost(30, SECONDS).untilAsserted(() -> {
            verify(notificationService).sendOrderCancelledNotification(any(OrderCancelledEvent.class));
        });
    }

    @Test
    void shouldHandleOrderErrorEvent() {
        String orderNumber = UUID.randomUUID().toString();

        var payload = new OrderErrorEvent(
                UUID.randomUUID().toString(),
                orderNumber,
                Set.of(),
                customer,
                address,
                "test error reason",
                LocalDateTime.now());

        Message message = MessageBuilder.withPayload(
                        new Event(OrderEventType.ORDER_PROCESSING_FAILED, payload.eventId(), payload))
                .setHeader("partitionKey", payload.eventId())
                .build();

        streamBridge.send("notification-out-0", message);

        await().atMost(30, SECONDS).untilAsserted(() -> {
            verify(notificationService).sendOrderErrorEventNotification(any(OrderErrorEvent.class));
        });
    }
}
