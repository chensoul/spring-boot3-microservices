package com.chensoul.ecommerce.core.recommendation.services;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import se.magnus.api.core.recommendation.Recommendation;
import com.chensoul.ecommerce.core.recommendation.persistence.RecommendationEntity;

@Mapper(componentModel = "spring")
public interface RecommendationMapper {

  @Mappings({
          @Mapping(target = "rate", source = "entity.rating"),
  })
  Recommendation entityToApi(RecommendationEntity entity);

  @Mappings({
          @Mapping(target = "rating", source = "api.rate"),
          @Mapping(target = "id", ignore = true),
          @Mapping(target = "version", ignore = true)
  })
  RecommendationEntity apiToEntity(Recommendation api);

  List<Recommendation> entityListToApiList(List<RecommendationEntity> entity);

  List<RecommendationEntity> apiListToEntityList(List<Recommendation> api);
}