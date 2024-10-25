package com.chensoul.bookstore.notification.event;

import com.chensoul.bookstore.notification.domain.NotificationService;
import com.chensoul.bookstore.notification.domain.OrderEventEntity;
import com.chensoul.bookstore.notification.domain.OrderEventRepository;
import com.chensoul.bookstore.order.OrderCancelledEvent;
import com.chensoul.bookstore.order.OrderCreatedEvent;
import com.chensoul.bookstore.order.OrderDeliveredEvent;
import com.chensoul.bookstore.order.OrderErrorEvent;
import com.chensoul.bookstore.order.OrderEventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class OrderEventHandler {
    private static final Logger log = LoggerFactory.getLogger(OrderEventHandler.class);

    private final NotificationService notificationService;
    private final OrderEventRepository orderEventRepository;

    public OrderEventHandler(NotificationService notificationService, OrderEventRepository orderEventRepository) {
        this.notificationService = notificationService;
        this.orderEventRepository = orderEventRepository;
    }

    @KafkaListener(topics = "${notification.new-order-queue}")
    public void handle(OrderCreatedEvent orderCreatedEvent) {
        if (orderEventRepository.existsByEventId(orderCreatedEvent.eventId())) {
            log.warn("Received duplicate OrderCreatedEvent with eventId: {}", orderCreatedEvent.eventId());
            return;
        }
        log.info("Received a OrderCreatedEvent with orderNumber:{}: ", orderCreatedEvent.orderNumber());
        notificationService.sendOrderCreatedNotification(orderCreatedEvent);
        var orderEvent = new OrderEventEntity(orderCreatedEvent.eventId(), OrderEventType.ORDER_CREATED);
        orderEventRepository.save(orderEvent);
    }

    @KafkaListener(topics = "${notification.delivered-order-queue}")
    public void handle(OrderDeliveredEvent orderDeliveredEvent) {
        if (orderEventRepository.existsByEventId(orderDeliveredEvent.eventId())) {
            log.warn("Received duplicate OrderDeliveredEvent with eventId: {}", orderDeliveredEvent.eventId());
            return;
        }
        log.info("Received a OrderDeliveredEvent with orderNumber:{}: ", orderDeliveredEvent.orderNumber());
        notificationService.sendOrderDeliveredNotification(orderDeliveredEvent);
        var orderEvent = new OrderEventEntity(orderDeliveredEvent.eventId(), OrderEventType.ORDER_DELIVERED);
        orderEventRepository.save(orderEvent);
    }

    @KafkaListener(topics = "${notification.cancelled-order-queue}")
    public void handle(OrderCancelledEvent orderCancelledEvent) {
        if (orderEventRepository.existsByEventId(orderCancelledEvent.eventId())) {
            log.warn("Received duplicate OrderCancelledEvent with eventId: {}", orderCancelledEvent.eventId());
            return;
        }
        notificationService.sendOrderCancelledNotification(orderCancelledEvent);
        log.info("Received a OrderCancelledEvent with orderNumber:{}: ", orderCancelledEvent.orderNumber());
        var orderEvent = new OrderEventEntity(orderCancelledEvent.eventId(), OrderEventType.ORDER_CANCELLED);
        orderEventRepository.save(orderEvent);
    }

    @KafkaListener(topics = "${notification.error-order-queue}")
    public void handle(OrderErrorEvent orderErrorEvent) {
        if (orderEventRepository.existsByEventId(orderErrorEvent.eventId())) {
            log.warn("Received duplicate OrderErrorEvent with eventId: {}", orderErrorEvent.eventId());
            return;
        }
        log.info("Received a OrderErrorEvent with orderNumber:{}: ", orderErrorEvent.orderNumber());
        notificationService.sendOrderErrorEventNotification(orderErrorEvent);
        OrderEventEntity orderEvent =
                new OrderEventEntity(orderErrorEvent.eventId(), OrderEventType.ORDER_PROCESSING_FAILED);
        orderEventRepository.save(orderEvent);
    }
}
