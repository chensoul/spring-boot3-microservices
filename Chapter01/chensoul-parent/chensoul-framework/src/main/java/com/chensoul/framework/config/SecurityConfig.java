package com.chensoul.framework.config;

import static org.springframework.security.config.Customizer.withDefaults;

import com.chensoul.framework.util.AuthoritiesConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@RequiredArgsConstructor
@Configuration
@EnableMethodSecurity(securedEnabled = true)
@ConditionalOnClass(SecurityFilterChain.class)
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz -> authz.requestMatchers(mvc.pattern(HttpMethod.POST, "/api/authenticate"))
                        .permitAll()
                        .requestMatchers(mvc.pattern(HttpMethod.GET, "/api/authenticate"))
                        .permitAll()
                        .requestMatchers(mvc.pattern("/api/admin/**"))
                        .hasAuthority(AuthoritiesConstants.ADMIN)
                        .requestMatchers(mvc.pattern("/api/**"))
                        .authenticated()
                        .requestMatchers(mvc.pattern("/v3/api-docs/**"))
                        .hasAuthority(AuthoritiesConstants.ADMIN)
                        .requestMatchers(mvc.pattern("/actuator/health"))
                        .permitAll()
                        .requestMatchers(mvc.pattern("/actuator/health/**"))
                        .permitAll()
                        .requestMatchers(mvc.pattern("/actuator/info"))
                        .permitAll()
                        .requestMatchers(mvc.pattern("/actuator/prometheus"))
                        .permitAll()
                        .requestMatchers(mvc.pattern("/actuator/**"))
                        .hasAuthority(AuthoritiesConstants.ADMIN))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
                        .accessDeniedHandler(new BearerTokenAccessDeniedHandler()))
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults()));
        return http.build();
    }

    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }
}
