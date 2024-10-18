package com.chensoul.ecommerce.producer;

import com.chensoul.ecommerce.payment.PaymentConfirmation;
import com.chensoul.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Slf4j
public class HttpNotificationProducer implements NotificationProducer {
    private final RestTemplate restTemplate;
    @Value("${application.config.notification-url}")
    private String notificationUrl;

    public void sendNotification(PaymentConfirmation request) {
        log.info("Sending payment notification");

        ResponseEntity<Void> responseEntity = restTemplate.postForEntity(notificationUrl + "/payment", request, Void.class);

        if (responseEntity.getStatusCode().isError()) {
            throw new BusinessException("An error occurred while processing the products purchase: " + responseEntity.getStatusCode());
        }
    }
}
