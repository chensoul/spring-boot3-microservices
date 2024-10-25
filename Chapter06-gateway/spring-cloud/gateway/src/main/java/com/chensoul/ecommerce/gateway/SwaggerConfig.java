package com.chensoul.ecommerce.gateway;

import java.util.HashSet;
import java.util.Set;
import org.springdoc.core.properties.AbstractSwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import static org.springdoc.core.utils.Constants.DEFAULT_API_DOCS_URL;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class SwaggerConfig {
    @Bean
    public ApplicationListener<RefreshRoutesEvent> swaggerRouteRefreshListener(
        SwaggerUiConfigProperties swaggerUiConfigProperties, RouteDefinitionLocator locator) {
        return new ApplicationListener<RefreshRoutesEvent>() {
            @Override
            public void onApplicationEvent(RefreshRoutesEvent event) {
                locator.getRouteDefinitions().collectList().subscribe(definitions -> {
                    Set<AbstractSwaggerUiConfigProperties.SwaggerUrl> urls = new HashSet<>();

                    definitions.stream()
                        .filter(routeDefinition -> routeDefinition.getId().matches(".*-service"))
                        .forEach(routeDefinition -> {
                            String name = routeDefinition.getId().replaceAll("-service", "");
                            AbstractSwaggerUiConfigProperties.SwaggerUrl swaggerUrl =
                                new AbstractSwaggerUiConfigProperties.SwaggerUrl(
                                    name, DEFAULT_API_DOCS_URL + "/" + name, null);
                            urls.add(swaggerUrl);
                        });
                    swaggerUiConfigProperties.setUrls(urls);
                });
            }
        };
    }
}
