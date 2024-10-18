package com.chensoul.ecommerce.producer;

import com.chensoul.ecommerce.event.Event;
import static com.chensoul.ecommerce.event.Event.Type.PAYMENT;
import com.chensoul.ecommerce.payment.PaymentConfirmation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class StreamNotificationProducer implements NotificationProducer {
    private final StreamBridge streamBridge;

    public void sendNotification(PaymentConfirmation paymentConfirmation) {
        sendMessage("notification-out-0", paymentConfirmation);
    }

    private void sendMessage(String bindingName, PaymentConfirmation paymentConfirmation) {
        log.info("Sending a {} message to {}", paymentConfirmation, bindingName);
        Message message = MessageBuilder.withPayload(new Event(PAYMENT, paymentConfirmation.orderId(), paymentConfirmation))
            .setHeader("partitionKey", paymentConfirmation.orderId())
            .build();
        streamBridge.send(bindingName, message);
    }
}
