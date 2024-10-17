package com.chensoul.ecommerce.core.review.services;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.RestController;
import se.magnus.api.core.review.Review;
import se.magnus.api.core.review.ReviewService;
import se.magnus.api.exception.InvalidInputException;
import com.chensoul.ecommerce.core.review.persistence.ReviewEntity;
import com.chensoul.ecommerce.core.review.persistence.ReviewRepository;

@RequiredArgsConstructor
@RestController
public class ReviewServiceImpl implements ReviewService {

  private static final Logger LOG = LoggerFactory.getLogger(ReviewServiceImpl.class);

  private final ReviewRepository repository;

  private final ReviewMapper mapper;

  @Override
  public Review createReview(Review body) {
    try {
      ReviewEntity entity = mapper.apiToEntity(body);
      ReviewEntity newEntity = repository.save(entity);

      LOG.info("createReview: created a review entity: {}/{}", body.getProductId(), body.getReviewId());
      return mapper.entityToApi(newEntity);

    } catch (DataIntegrityViolationException dive) {
      throw new InvalidInputException("Duplicate key, Product Id: " + body.getProductId() + ", Review Id:" + body.getReviewId());
    }
  }

  @Override
  public List<Review> getReviews(int productId) {

    if (productId < 1) {
      throw new InvalidInputException("Invalid productId: " + productId);
    }

    if (productId == 213) {
      LOG.info("No reviews found for productId: {}", productId);
      return new ArrayList<>();
    }

    List<Review> list = new ArrayList<>();
    list.add(new Review(productId, 1, "Author 1", "Subject 1", "Content 1"));
    list.add(new Review(productId, 2, "Author 2", "Subject 2", "Content 2"));
    list.add(new Review(productId, 3, "Author 3", "Subject 3", "Content 3"));

    LOG.info("/reviews response size: {}", list.size());

    return list;
  }

  @Override
  public void deleteReviews(int productId) {
    LOG.info("deleteReviews: tries to delete reviews for the product with productId: {}", productId);
    repository.deleteAll(repository.findByProductId(productId));
  }
}
