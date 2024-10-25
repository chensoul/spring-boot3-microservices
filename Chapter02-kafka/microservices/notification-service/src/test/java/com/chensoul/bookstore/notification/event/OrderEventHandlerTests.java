package com.chensoul.bookstore.notification.event;

import com.chensoul.bookstore.notification.AbstractIT;
import com.chensoul.bookstore.notification.ApplicationProperties;
import com.chensoul.bookstore.order.Address;
import com.chensoul.bookstore.order.Customer;
import com.chensoul.bookstore.order.OrderCancelledEvent;
import com.chensoul.bookstore.order.OrderCreatedEvent;
import com.chensoul.bookstore.order.OrderDeliveredEvent;
import com.chensoul.bookstore.order.OrderErrorEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

class OrderEventHandlerTests extends AbstractIT {
    @Autowired
    KafkaTemplate kafkaTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ApplicationProperties properties;

    Customer customer = new Customer("Chensoul", "ichensoul@gmail.com", "999999999");
    Address address = new Address("addr line 1", null, "Hyderabad", "TS", "500072", "India");

    @Test
    void shouldHandleOrderCreatedEvent() throws JsonProcessingException {
        String orderNumber = UUID.randomUUID().toString();

        OrderCreatedEvent event = new OrderCreatedEvent(
            UUID.randomUUID().toString(), orderNumber, Set.of(), customer, address, LocalDateTime.now());

        kafkaTemplate.send(properties.newOrderQueue(), event);

        await().atMost(30, SECONDS).untilAsserted(() -> {
            verify(notificationService).sendOrderCreatedNotification(any(OrderCreatedEvent.class));
        });
    }

    @Test
    void shouldHandleOrderDeliveredEvent() {
        String orderNumber = UUID.randomUUID().toString();

        var event = new OrderDeliveredEvent(
            UUID.randomUUID().toString(), orderNumber, Set.of(), customer, address, LocalDateTime.now());
        kafkaTemplate.send(properties.deliveredOrderQueue(), event);

        await().atMost(30, SECONDS).untilAsserted(() -> {
            verify(notificationService).sendOrderDeliveredNotification(any(OrderDeliveredEvent.class));
        });
    }

    @Test
    void shouldHandleOrderCancelledEvent() {
        String orderNumber = UUID.randomUUID().toString();

        var event = new OrderCancelledEvent(
            UUID.randomUUID().toString(),
            orderNumber,
            Set.of(),
            customer,
            address,
            "test cancel reason",
            LocalDateTime.now());
        kafkaTemplate.send(properties.cancelledOrderQueue(), event);

        await().atMost(30, SECONDS).untilAsserted(() -> {
            verify(notificationService).sendOrderCancelledNotification(any(OrderCancelledEvent.class));
        });
    }

    @Test
    void shouldHandleOrderErrorEvent() {
        String orderNumber = UUID.randomUUID().toString();

        var event = new OrderErrorEvent(
            UUID.randomUUID().toString(),
            orderNumber,
            Set.of(),
            customer,
            address,
            "test error reason",
            LocalDateTime.now());
        kafkaTemplate.send(properties.errorOrderQueue(), event);

        await().atMost(30, SECONDS).untilAsserted(() -> {
            verify(notificationService).sendOrderErrorEventNotification(any(OrderErrorEvent.class));
        });
    }
}
