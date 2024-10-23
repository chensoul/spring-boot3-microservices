package com.chensoul.framework.config;

import static org.springdoc.core.utils.SpringDocUtils.getConfig;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import java.nio.ByteBuffer;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springdoc.core.customizers.ServerBaseUrlCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.lang.Nullable;

@Profile("!prod")
@Slf4j
@Configuration
@ConditionalOnWebApplication
@ConditionalOnClass(OpenAPI.class)
public class SpringDocConfiguration {
    static {
        getConfig().replaceWithClass(ByteBuffer.class, String.class);
    }

    @Value("${spring.application.name:application}")
    private String applicationName;

    @Bean
    public OpenAPI openAPI(ApplicationProperties.ApiDocs apiDocs, @Nullable BuildProperties buildProperties) {
        log.info("Initializing OpenApi");

        String buildArtifact =
                buildProperties != null ? buildProperties.getArtifact().replaceAll("-", " ") : applicationName;
        String buildVersion = buildProperties != null ? buildProperties.getVersion() : "0.0.1";

        String title = StringUtils.defaultIfBlank(apiDocs.getTitle(), StringUtils.capitalize(buildArtifact) + " API");
        String version = StringUtils.defaultIfBlank(apiDocs.getVersion(), buildVersion);
        String description = StringUtils.defaultIfBlank(apiDocs.getDescription(), "This is the REST API for " + title);

        Info info = new Info()
                .title(title)
                .description(description)
                .version(version)
                .license(new License().name(apiDocs.getLicense()).url(apiDocs.getLicenseUrl()))
                .termsOfService(apiDocs.getTermsOfServiceUrl());

        if (apiDocs.getContactName() != null) {
            Contact contact = new Contact()
                    .name(apiDocs.getContactName())
                    .url(apiDocs.getContactUrl())
                    .email(apiDocs.getContactEmail());
            info.contact(contact);
        }

        OpenAPI openAPI = new OpenAPI();
        openAPI.info(info);

        if (apiDocs.getServers() != null) {
            for (ApplicationProperties.ApiDocs.Server server : apiDocs.getServers()) {
                openAPI.addServersItem(new Server().url(server.getUrl()).description(server.getDescription()));
            }
        }

        return openAPI;
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
