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
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationRepository repository;
    private final EmailService emailService;

    @SneakyThrows
    public void consumePaymentSuccessNotifications(PaymentConfirmation paymentConfirmation) {
        log.info(format("Consuming the message from payment-topic Topic:: %s", paymentConfirmation));
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
    }

    @SneakyThrows
    public void consumeOrderConfirmationNotifications(OrderConfirmation orderConfirmation) {
        log.info(format("Consuming the message from order-topic Topic:: %s", orderConfirmation));
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
    }
}
