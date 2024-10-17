package com.chensoul.ecommerce.composite.product;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
    http
            .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                    .requestMatchers("/openapi/**").permitAll()
                    .requestMatchers("/webjars/**").permitAll()
                    .requestMatchers("/actuator/**").permitAll()
                    .requestMatchers(POST, "/product-composite/**").hasAuthority("SCOPE_product:write")
                    .requestMatchers(DELETE, "/product-composite/**").hasAuthority("SCOPE_product:write")
                    .requestMatchers(GET, "/product-composite/**").hasAuthority("SCOPE_product:read")
                    .anyRequest().authenticated()
            )
            .oauth2ResourceServer(spec -> spec.jwt(Customizer.withDefaults()));
    return http.build();
  }
}