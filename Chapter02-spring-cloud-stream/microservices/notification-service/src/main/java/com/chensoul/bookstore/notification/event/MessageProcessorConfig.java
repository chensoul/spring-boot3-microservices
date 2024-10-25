package com.chensoul.bookstore.notification.event;

import com.chensoul.bookstore.event.Event;
import com.chensoul.bookstore.order.OrderCancelledEvent;
import com.chensoul.bookstore.order.OrderCreatedEvent;
import com.chensoul.bookstore.order.OrderEventType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageProcessorConfig {
    private static final Logger log = LoggerFactory.getLogger(MessageProcessorConfig.class);

    private final OrderEventHandler orderEventHandler;
    private final ObjectMapper objectMapper;

    public MessageProcessorConfig(OrderEventHandler orderEventHandler, ObjectMapper objectMapper) {
        this.orderEventHandler = orderEventHandler;
        this.objectMapper = objectMapper;
    }

    @Bean
    public Consumer<Event> messageProcessor() {
        return event -> {
            log.info("Process message {}...", event.payload());

            switch (event.orderEventType()) {
                case OrderEventType.ORDER_CREATED:
                    OrderCreatedEvent orderCreatedEvent = null;
                    try {
                        orderCreatedEvent = objectMapper.readValue(
                                objectMapper.writeValueAsString(event.payload()), OrderCreatedEvent.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    orderEventHandler.handle(orderCreatedEvent);
                    break;

                case OrderEventType.ORDER_CANCELLED:
                    OrderCancelledEvent orderCancelledEvent = null;
                    try {
                        orderCancelledEvent = objectMapper.readValue(
                                objectMapper.writeValueAsString(event.payload()), OrderCancelledEvent.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    orderEventHandler.handle(orderCancelledEvent);
                    break;
            }

            log.info("Message processing done!");
        };
    }
}
