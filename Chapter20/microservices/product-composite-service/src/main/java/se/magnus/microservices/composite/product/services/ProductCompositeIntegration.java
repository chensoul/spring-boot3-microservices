package com.chensoul.ecommerce.composite.product.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.core.ParameterizedTypeReference;
import static org.springframework.http.HttpMethod.GET;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import se.magnus.api.core.product.Product;
import se.magnus.api.core.product.ProductService;
import se.magnus.api.core.recommendation.Recommendation;
import se.magnus.api.core.recommendation.RecommendationService;
import se.magnus.api.core.review.Review;
import se.magnus.api.core.review.ReviewService;
import se.magnus.api.event.Event;
import static se.magnus.api.event.Event.Type.CREATE;
import static se.magnus.api.event.Event.Type.DELETE;
import se.magnus.api.exception.InvalidInputException;
import se.magnus.api.exception.NotFoundException;
import se.magnus.util.http.HttpErrorInfo;

@RequiredArgsConstructor
@Component
public class ProductCompositeIntegration implements ProductService, RecommendationService, ReviewService {

  private static final Logger LOG = LoggerFactory.getLogger(ProductCompositeIntegration.class);
  private static final String PRODUCT_SERVICE_URL = "http://product";
  private static final String RECOMMENDATION_SERVICE_URL = "http://recommendation";
  private static final String REVIEW_SERVICE_URL = "http://review";

  private final RestTemplate restTemplate;
  private final ObjectMapper mapper;
  private final StreamBridge streamBridge;

  @Override
  public Product createProduct(Product body) {
    sendMessage("products-out-0", new Event(CREATE, body.getProductId(), body));
    return body;
  }

  @Override
  @Retry(name = "product")
  @TimeLimiter(name = "product")
  @CircuitBreaker(name = "product", fallbackMethod = "getProductFallbackValue")
  public Product getProduct(int productId) {
    try {
      String url = PRODUCT_SERVICE_URL + "/product/" + productId;
      LOG.info("Will call the getProduct API on URL: {}", url);

      Product product = restTemplate.getForObject(url, Product.class);
      LOG.info("Found a product with id: {}", product.getProductId());

      return product;

    } catch (HttpClientErrorException ex) {
      throw handleHttpClientException(ex);
    }
  }

  private Product getProductFallbackValue(int productId, CallNotPermittedException ex) {
    LOG.warn("Creating a fail-fast fallback product for productId = {}, delay = {}, faultPercent = {} and exception = {} ",
            productId, ex.toString());

    if (productId == 13) {
      String errMsg = "Product Id: " + productId + " not found in fallback cache!";
      LOG.warn(errMsg);
      throw new NotFoundException(errMsg);
    }

    return new Product(productId, "Fallback product" + productId, productId);
  }

  @Override
  public void deleteProduct(int productId) {
    sendMessage("products-out-0", new Event(DELETE, productId, null));
  }

  @Override
  public Recommendation createRecommendation(Recommendation body) {
    sendMessage("recommendations-out-0", new Event(CREATE, body.getProductId(), body));
    return body;
  }

  @Override
  public List<Recommendation> getRecommendations(int productId) {
    try {
      String url = RECOMMENDATION_SERVICE_URL + "/recommendation?productId=" + productId;
      LOG.info("Will call the getRecommendations API on URL: {}", url);
      List<Recommendation> recommendations = restTemplate
              .exchange(url, GET, null, new ParameterizedTypeReference<List<Recommendation>>() {
              })
              .getBody();

      LOG.info("Found {} recommendations for a product with id: {}", recommendations.size(), productId);
      return recommendations;

    } catch (Exception ex) {
      LOG.warn("Got an exception while requesting recommendations, return zero recommendations: {}", ex.getMessage());
      return new ArrayList<>();
    }
  }

  @Override
  public void deleteRecommendations(int productId) {
    sendMessage("recommendations-out-0", new Event(DELETE, productId, null));
  }

  @Override
  public Review createReview(Review body) {
    sendMessage("reviews-out-0", new Event(CREATE, body.getProductId(), body));
    return body;
  }

  @Override
  public List<Review> getReviews(int productId) {
    try {
      String url = REVIEW_SERVICE_URL + "/review?productId=" + productId;
      LOG.info("Will call the getReviews API on URL: {}", url);
      List<Review> reviews = restTemplate
              .exchange(url, GET, null, new ParameterizedTypeReference<List<Review>>() {
              })
              .getBody();

      LOG.info("Found {} reviews for a product with id: {}", reviews.size(), productId);
      return reviews;

    } catch (Exception ex) {
      LOG.warn("Got an exception while requesting reviews, return zero reviews: {}", ex.getMessage());
      return new ArrayList<>();
    }
  }

  @Override
  public void deleteReviews(int productId) {
    sendMessage("reviews-out-0", new Event(DELETE, productId, null));
  }


  private Health getHealth(String url) {
    url += "/actuator/health";
    LOG.info("Will call the Health API on URL: {}", url);

    ResponseEntity<String> forEntity = restTemplate.getForEntity(url, String.class);
    return forEntity.getStatusCode() == HttpStatus.OK
            ? Health.up().build()
            : Health.down().build();
  }

  private RuntimeException handleHttpClientException(HttpClientErrorException ex) {
    switch (HttpStatus.resolve(ex.getStatusCode().value())) {
      case NOT_FOUND:
        return new NotFoundException(getErrorMessage(ex));
      case UNPROCESSABLE_ENTITY:
        return new InvalidInputException(getErrorMessage(ex));
      default:
        LOG.warn("Got an unexpected HTTP error: {}, will rethrow it", ex.getStatusCode());
        LOG.warn("Error body: {}", ex.getResponseBodyAsString());
        return ex;
    }
  }

  private void sendMessage(String bindingName, Event event) {
    LOG.info("Sending a {} message to {}", event.getEventType(), bindingName);
    Message message = MessageBuilder.withPayload(event)
            .setHeader("partitionKey", event.getKey())
            .build();
    streamBridge.send(bindingName, message);
  }

  private String getErrorMessage(HttpClientErrorException ex) {
    try {
      return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
    } catch (IOException ioex) {
      return ex.getMessage();
    }
  }
}