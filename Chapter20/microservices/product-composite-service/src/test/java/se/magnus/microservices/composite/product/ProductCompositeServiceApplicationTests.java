package com.chensoul.ecommerce.composite.product;

import static java.util.Collections.singletonList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.magnus.api.core.product.Product;
import se.magnus.api.core.recommendation.Recommendation;
import se.magnus.api.core.review.Review;
import se.magnus.api.exception.InvalidInputException;
import se.magnus.api.exception.NotFoundException;
import com.chensoul.ecommerce.composite.product.services.ProductCompositeIntegration;

//@SpringBootTest(
webEnvironment =RANDOM_PORT,
classes ={TestSecurityConfig .class},
properties ={
    "spring.security.oauth2.resourceserver.jwt.issuer-uri=",
    "spring.main.allow-bean-definition-overriding=true",
    "eureka.client.enabled=false",
    "spring.cloud.config.enabled=false"})

class ProductCompositeServiceApplicationTests {

    private static final int PRODUCT_ID_OK = 1;
    private static final int PRODUCT_ID_NOT_FOUND = 2;
    private static final int PRODUCT_ID_INVALID = 3;

    @Autowired
    private WebTestClient client;

    @MockBean
    private ProductCompositeIntegration compositeIntegration;

    @BeforeEach
    void setUp() {

        when(compositeIntegration.getProduct(PRODUCT_ID_OK))
            .thenReturn(new Product(PRODUCT_ID_OK, "name", 1));
        when(compositeIntegration.getRecommendations(PRODUCT_ID_OK))
            .thenReturn(singletonList(new Recommendation(PRODUCT_ID_OK, 1, "author", 1, "content")));
        when(compositeIntegration.getReviews(PRODUCT_ID_OK))
            .thenReturn(singletonList(new Review(PRODUCT_ID_OK, 1, "author", "subject", "content")));

        when(compositeIntegration.getProduct(PRODUCT_ID_NOT_FOUND))
            .thenThrow(new NotFoundException("NOT FOUND: " + PRODUCT_ID_NOT_FOUND));

        when(compositeIntegration.getProduct(PRODUCT_ID_INVALID))
            .thenThrow(new InvalidInputException("INVALID: " + PRODUCT_ID_INVALID));
    }

    @Test
    void contextLoads() {
    }

    @Test
    void getProductById() {

        client.get()
            .uri("/product-composite/" + PRODUCT_ID_OK)
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.productId").isEqualTo(PRODUCT_ID_OK)
            .jsonPath("$.recommendations.length()").isEqualTo(1)
            .jsonPath("$.reviews.length()").isEqualTo(1);
    }

    @Test
    void getProductNotFound() {

        client.get()
            .uri("/product-composite/" + PRODUCT_ID_NOT_FOUND)
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isNotFound()
            .expectHeader().contentType(APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.path").isEqualTo("/product-composite/" + PRODUCT_ID_NOT_FOUND)
            .jsonPath("$.message").isEqualTo("NOT FOUND: " + PRODUCT_ID_NOT_FOUND);
    }

    @Test
    void getProductInvalidInput() {

        client.get()
            .uri("/product-composite/" + PRODUCT_ID_INVALID)
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().isEqualTo(UNPROCESSABLE_ENTITY)
            .expectHeader().contentType(APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.path").isEqualTo("/product-composite/" + PRODUCT_ID_INVALID)
            .jsonPath("$.message").isEqualTo("INVALID: " + PRODUCT_ID_INVALID);
    }
}
