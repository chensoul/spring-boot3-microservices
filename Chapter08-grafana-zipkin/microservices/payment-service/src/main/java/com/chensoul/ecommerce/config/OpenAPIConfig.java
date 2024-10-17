package com.chensoul.ecommerce.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

  @Bean
  public OpenAPI orderServiceAPI() {
    return new OpenAPI()
            .info(new Info().title("Payment Service API")
                    .description("This is the REST API for Payment Service")
                    .version("v0.0.1")
                    .license(new License().name("Apache 2.0")))
            .externalDocs(new ExternalDocumentation()
                    .description("You can refer to the Payment Service Wiki Documentation")
                    .url("https://customer-service-dummy-url.com/docs"));
  }
}