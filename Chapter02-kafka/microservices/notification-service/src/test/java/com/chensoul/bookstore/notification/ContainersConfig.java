package com.chensoul.bookstore.notification;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class ContainersConfig {
    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:17-alpine"));
    }

    @Bean
    @ServiceConnection
    KafkaContainer rabbitContainer() {
        return new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.7.1"));
    }

    @Bean
    GenericContainer<?> mailhogContainer(DynamicPropertyRegistry registry) {
        var container = new GenericContainer<>(DockerImageName.parse("mailhog/mailhog:v1.0.1")).withExposedPorts(1025);
        container.start();
        registry.add("spring.mail.host", container::getContainerIpAddress);
        registry.add("spring.mail.port", container::getFirstMappedPort);
        return container;
    }
}
