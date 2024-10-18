package com.chensoul.ecommerce.producer;

import com.chensoul.ecommerce.order.OrderConfirmation;
import com.chensoul.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class HttpOrderProducer implements OrderProducer {
    private final RestTemplate restTemplate;
    @Value("${application.config.notification-url}")
    private String notificationUrl;

    public void sendNotification(OrderConfirmation orderConfirmation) {
        log.info("Sending order confirmation");

        ResponseEntity<Void> responseEntity = restTemplate.postForEntity(notificationUrl + "/order", orderConfirmation, Void.class);

        if (responseEntity.getStatusCode().isError()) {
            throw new BusinessException("An error occurred while processing the products purchase: " + responseEntity.getStatusCode());
        }
    }
}
