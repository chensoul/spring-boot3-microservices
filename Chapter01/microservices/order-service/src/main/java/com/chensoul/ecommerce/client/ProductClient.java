package com.chensoul.ecommerce.client;

import com.chensoul.exception.BusinessException;
import com.chensoul.ecommerce.product.ProductPurchaseRequest;
import com.chensoul.ecommerce.product.ProductPurchaseResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class ProductClient {

    private final RestTemplate restTemplate;
    @Value("${application.config.product-url}")
    private String productUrl;

    public List<ProductPurchaseResponse> purchaseProducts(List<ProductPurchaseRequest> requestBody) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(CONTENT_TYPE, APPLICATION_JSON_VALUE);

        HttpEntity<List<ProductPurchaseRequest>> requestEntity = new HttpEntity<>(requestBody, headers);
        ParameterizedTypeReference<List<ProductPurchaseResponse>> responseType = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<List<ProductPurchaseResponse>> responseEntity = restTemplate.exchange(
            productUrl + "/purchase",
            POST,
            requestEntity,
            responseType
        );

        if (responseEntity.getStatusCode().isError()) {
            throw new BusinessException("An error occurred while processing the products purchase: " + responseEntity.getStatusCode());
        }
        return responseEntity.getBody();
    }

}
