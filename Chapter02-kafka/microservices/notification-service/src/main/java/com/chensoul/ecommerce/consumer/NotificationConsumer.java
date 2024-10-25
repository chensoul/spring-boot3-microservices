package com.chensoul.ecommerce.consumer;

import com.chensoul.ecommerce.email.EmailService;
import com.chensoul.ecommerce.notification.Notification;
import com.chensoul.ecommerce.notification.NotificationRepository;
import static com.chensoul.ecommerce.notification.NotificationType.ORDER_CONFIRMATION;
import static com.chensoul.ecommerce.notification.NotificationType.PAYMENT_CONFIRMATION;
import com.chensoul.ecommerce.order.OrderConfirmation;
import com.chensoul.ecommerce.payment.PaymentConfirmation;
import static java.lang.String.format;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationRepository repository;
    private final EmailService emailService;
    private final ObservationRegistry observationRegistry;
    private final Tracer tracer;

    @SneakyThrows
    @KafkaListener(topics = "payment-topic")
    public void consumePaymentSuccessNotifications(PaymentConfirmation paymentConfirmation) {
        Observation.createNotStarted("on-message", this.observationRegistry).observe(() -> {
            log.info("TraceId- {}, Received Notification for Payment - {}", this.tracer.currentSpan().context().traceId(),
                paymentConfirmation);

            repository.save(
                Notification.builder()
                    .type(PAYMENT_CONFIRMATION)
                    .createdDate(LocalDateTime.now())
                    .paymentConfirmation(paymentConfirmation)
                    .build()
            );
            String customerName = paymentConfirmation.customerFirstname() + " " + paymentConfirmation.customerLastname();
            emailService.sendPaymentSuccessEmail(
                paymentConfirmation.customerEmail(),
                customerName,
                paymentConfirmation.amount(),
                paymentConfirmation.orderId()
            );
        });
    }

    @SneakyThrows
    @KafkaListener(topics = "order-topic")
    public void consumeOrderConfirmationNotifications(OrderConfirmation orderConfirmation) {
        Observation.createNotStarted("on-message", this.observationRegistry).observe(() -> {
            log.info("TraceId- {}, Received Notification for Order - {}", this.tracer.currentSpan().context().traceId(),
                orderConfirmation);

            repository.save(
                Notification.builder()
                    .type(ORDER_CONFIRMATION)
                    .createdDate(LocalDateTime.now())
                    .orderConfirmation(orderConfirmation)
                    .build()
            );
            String customerName = orderConfirmation.customerResponse().firstname() + " " + orderConfirmation.customerResponse().lastname();
            emailService.sendOrderConfirmationEmail(
                orderConfirmation.customerResponse().email(),
                customerName,
                orderConfirmation.totalAmount(),
                orderConfirmation.orderId(),
                orderConfirmation.productResponses()
            );
        });

    }
}
