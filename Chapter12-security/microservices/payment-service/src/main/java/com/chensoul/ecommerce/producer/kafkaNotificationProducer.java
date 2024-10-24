package com.chensoul.ecommerce.producer;

import static org.springframework.kafka.support.KafkaHeaders.TOPIC;

import com.chensoul.ecommerce.payment.PaymentConfirmation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class kafkaNotificationProducer implements NotificationProducer {
    private final KafkaTemplate<String, PaymentConfirmation> kafkaTemplate;

    public void sendNotification(PaymentConfirmation request) {
        log.info("Sending notification with body = < {} >", request);
        Message<PaymentConfirmation> message = MessageBuilder.withPayload(request)
                .setHeader(TOPIC, "payment-topic")
                .build();

        kafkaTemplate.send(message);
    }
}
