package com.chensoul.ecommerce.consumer;

import com.chensoul.ecommerce.event.Event;
import com.chensoul.ecommerce.order.OrderConfirmation;
import com.chensoul.ecommerce.payment.PaymentConfirmation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class MessageProcessorConfig {

  private final NotificationConsumer notificationConsumer;
  private final ObjectMapper objectMapper;

  @Bean
  public Consumer<Event<Integer, Object>> messageProcessor() {
    return event -> {
      log.info("Process message created at {}...", event.getEventCreatedAt());

      switch (event.getEventType()) {
        case ORDER:
          OrderConfirmation recommendation = null;
          try {
            recommendation = objectMapper.readValue(objectMapper.writeValueAsString(event.getData()), OrderConfirmation.class);
          } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
          }
          log.info("OrderConfirmation with orderId: {}", recommendation.orderId());
          notificationConsumer.consumeOrderConfirmationNotifications(recommendation);
          break;

        case PAYMENT:
          PaymentConfirmation paymentConfirmation = null;
          try {
            paymentConfirmation = objectMapper.readValue(objectMapper.writeValueAsString(event.getData()), PaymentConfirmation.class);
          } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
          }
          log.info("PaymentConfirmation with orderId: {}", paymentConfirmation.orderId());
          notificationConsumer.consumePaymentSuccessNotifications(paymentConfirmation);
          break;
      }

      log.info("Message processing done!");
    };
  }
}
