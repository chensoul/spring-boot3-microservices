package com.chensoul.framework.config;

import static com.chensoul.framework.AppConstants.PROFILE_NOT_PROD;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import java.nio.ByteBuffer;
import java.util.List;
import org.springdoc.core.customizers.ServerBaseUrlCustomizer;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile(PROFILE_NOT_PROD)
@Configuration(proxyBeanMethods = false)
@OpenAPIDefinition(info = @Info(title = "app", version = "v1"))
class OpenApiConfig {
    static {
        SpringDocUtils.getConfig().replaceWithClass(ByteBuffer.class, String.class);
    }

    @Bean
    public ServerBaseUrlCustomizer serverBaseUrlRequestCustomizer() {
        return (serverBaseUrl, request) -> {
            List<String> forwardedPrefix = request.getHeaders().get("X-Forwarded-Prefix");
            if (forwardedPrefix != null && forwardedPrefix.size() > 0) {
                return forwardedPrefix.get(0);
            }
            return serverBaseUrl;
        };
    }
}
