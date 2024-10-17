package com.chensoul.ecommerce.core.product.services;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;
import se.magnus.api.core.product.Product;
import se.magnus.api.core.product.ProductService;
import se.magnus.api.exception.InvalidInputException;
import se.magnus.api.exception.NotFoundException;
import com.chensoul.ecommerce.core.product.persistence.ProductEntity;
import com.chensoul.ecommerce.core.product.persistence.ProductRepository;

@RequiredArgsConstructor
@RestController
public class ProductServiceImpl implements ProductService {

  private static final Logger LOG = LoggerFactory.getLogger(ProductServiceImpl.class);

  private final ProductRepository repository;
  private final ProductMapper mapper;

  @Override
  public Product createProduct(Product body) {
    try {
      ProductEntity entity = mapper.apiToEntity(body);
      ProductEntity newEntity = repository.save(entity);

      LOG.info("createProduct: entity created for productId: {}", body.getProductId());
      return mapper.entityToApi(newEntity);

    } catch (DuplicateKeyException dke) {
      throw new InvalidInputException("Duplicate key, Product Id: " + body.getProductId());
    }
  }

  @Override
  public void deleteProduct(int productId) {
    LOG.info("deleteProduct: tries to delete an entity with productId: {}", productId);
    repository.findByProductId(productId).ifPresent(e -> repository.delete(e));
  }

  @Override
  public Product getProduct(int productId) {
    if (productId < 1) {
      throw new InvalidInputException("Invalid productId: " + productId);
    }

    LOG.info("Will get product info for id={}", productId);

    Optional<ProductEntity> productEntity = repository.findByProductId(productId);
    if (productEntity.isPresent()) {
      Product product = mapper.entityToApi(productEntity.get());
      LOG.info("Got product info for id={}", productId);
      return product;
    }
    throw new NotFoundException("No product found for productId: " + productId);
  }
}
