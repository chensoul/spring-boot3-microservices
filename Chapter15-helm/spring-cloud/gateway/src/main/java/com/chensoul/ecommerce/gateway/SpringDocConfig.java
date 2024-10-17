package com.chensoul.ecommerce.gateway;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.properties.AbstractSwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class SpringDocConfig {

  /**
   * 动态配置 swagger 路由
   *
   * @param swaggerUiConfigProperties
   * @param locator
   * @return
   */
  @Bean("swaggerRouteRefreshListener")
  public ApplicationListener<RefreshRoutesEvent> routeRefreshListener(SwaggerUiConfigProperties swaggerUiConfigProperties, RouteDefinitionLocator locator) {
    return new ApplicationListener<RefreshRoutesEvent>() {
      @Override
      public void onApplicationEvent(RefreshRoutesEvent event) {
        locator.getRouteDefinitions().collectList().subscribe(definitions -> {
          List<RouteDefinition> routeDefinitions = definitions.stream().filter(routeDefinition -> routeDefinition.getId().matches(".*-service")).collect(Collectors.toList());

          log.info("Refreshing swagger routes: {}", routeDefinitions.stream().map(RouteDefinition::getId).collect(Collectors.toList()));

          if (swaggerUiConfigProperties.getUrls() == null) {
            swaggerUiConfigProperties.setUrls(new LinkedHashSet<>());
          }

          routeDefinitions.forEach(routeDefinition -> {
            String group = routeDefinition.getId();
            AbstractSwaggerUiConfigProperties.SwaggerUrl url = new AbstractSwaggerUiConfigProperties.SwaggerUrl(
                    group, "/" + group + "/v3/api-docs", group
            );
            swaggerUiConfigProperties.getUrls().add(url);
          });
        });
      }
    };
  }
}
