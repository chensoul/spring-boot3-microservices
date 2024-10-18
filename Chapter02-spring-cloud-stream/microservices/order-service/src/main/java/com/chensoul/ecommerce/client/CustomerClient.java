package com.chensoul.ecommerce.client;

import com.chensoul.ecommerce.customer.CustomerResponse;
import com.chensoul.exception.BusinessException

    ;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class CustomerClient {
    private final RestTemplate restTemplate;
    @Value("${application.config.customer-url}")
    private String customerUrl;

    public Optional<CustomerResponse> findCustomerById(String customerId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(CONTENT_TYPE, APPLICATION_JSON_VALUE);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<CustomerResponse> responseEntity = restTemplate.exchange(
            customerUrl + "/" + customerId,
            GET,
            requestEntity,
            CustomerResponse.class
        );

        if (responseEntity.getStatusCode().isError()) {
            throw new BusinessException("An error occurred while processing the customer request: " + responseEntity.getStatusCode());
        }
        return Optional.ofNullable(responseEntity.getBody());
    }
}
