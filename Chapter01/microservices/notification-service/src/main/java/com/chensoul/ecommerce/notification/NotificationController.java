package com.chensoul.ecommerce.notification;

import com.chensoul.ecommerce.consumer.NotificationConsumer;
import com.chensoul.ecommerce.order.OrderConfirmation;
import com.chensoul.ecommerce.payment.PaymentConfirmation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationConsumer service;

    @PostMapping("/payment")
    public ResponseEntity<Void> consumePaymentSuccessNotifications(@RequestBody @Valid PaymentConfirmation paymentConfirmation) {
        this.service.consumePaymentSuccessNotifications(paymentConfirmation);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/order")
    public ResponseEntity<Void> consumeOrderConfirmationNotifications(@RequestBody @Valid OrderConfirmation orderConfirmation) {
        this.service.consumeOrderConfirmationNotifications(orderConfirmation);
        return ResponseEntity.ok().build();
    }

}
