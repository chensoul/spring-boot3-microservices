package com.chensoul.ecommerce.client;

import com.chensoul.ecommerce.exception.BusinessException;
import com.chensoul.ecommerce.payment.PaymentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class PaymentClient {
  private final RestTemplate restTemplate;
  @Value("${application.config.payment-url}")
  private String paymentUrl;

  public Integer requestOrderPayment(@RequestBody PaymentRequest requestBody) {
    HttpHeaders headers = new HttpHeaders();
    headers.set(CONTENT_TYPE, APPLICATION_JSON_VALUE);

    HttpEntity<PaymentRequest> requestEntity = new HttpEntity<>(requestBody, headers);

    ResponseEntity<Integer> responseEntity = restTemplate.exchange(
            paymentUrl,
            POST,
            requestEntity,
            Integer.class
    );

    if (responseEntity.getStatusCode().isError()) {
      throw new BusinessException("An error occurred while processing the payment purchase: " + responseEntity.getStatusCode());
    }
    return responseEntity.getBody();
  }
}
