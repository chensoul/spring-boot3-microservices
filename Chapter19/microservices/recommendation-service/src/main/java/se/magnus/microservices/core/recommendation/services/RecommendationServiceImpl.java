package com.chensoul.ecommerce.core.recommendation.services;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;
import se.magnus.api.core.recommendation.Recommendation;
import se.magnus.api.core.recommendation.RecommendationService;
import se.magnus.api.exception.InvalidInputException;
import com.chensoul.ecommerce.core.recommendation.persistence.RecommendationEntity;
import com.chensoul.ecommerce.core.recommendation.persistence.RecommendationRepository;

@RequiredArgsConstructor
@RestController
public class RecommendationServiceImpl implements RecommendationService {

  private static final Logger LOG = LoggerFactory.getLogger(RecommendationServiceImpl.class);

  private final RecommendationRepository repository;

  private final RecommendationMapper mapper;

  @Override
  public Recommendation createRecommendation(Recommendation body) {
    try {
      RecommendationEntity entity = mapper.apiToEntity(body);
      RecommendationEntity newEntity = repository.save(entity);

      LOG.info("createRecommendation: created a recommendation entity: {}/{}", body.getProductId(), body.getRecommendationId());
      return mapper.entityToApi(newEntity);

    } catch (DuplicateKeyException dke) {
      throw new InvalidInputException("Duplicate key, Product Id: " + body.getProductId() + ", Recommendation Id:" + body.getRecommendationId());
    }
  }

  @Override
  public List<Recommendation> getRecommendations(int productId) {

    if (productId < 1) {
      throw new InvalidInputException("Invalid productId: " + productId);
    }

    if (productId == 113) {
      LOG.info("No recommendations found for productId: {}", productId);
      return new ArrayList<>();
    }

    List<Recommendation> list = new ArrayList<>();
    list.add(new Recommendation(productId, 1, "Author 1", 1, "Content 1"));
    list.add(new Recommendation(productId, 2, "Author 2", 2, "Content 2"));
    list.add(new Recommendation(productId, 3, "Author 3", 3, "Content 3"));

    LOG.info("/recommendation response size: {}", list.size());

    return list;
  }

  @Override
  public void deleteRecommendations(int productId) {
    LOG.info("deleteRecommendations: tries to delete recommendations for the product with productId: {}", productId);
    repository.deleteAll(repository.findByProductId(productId));
  }
}
