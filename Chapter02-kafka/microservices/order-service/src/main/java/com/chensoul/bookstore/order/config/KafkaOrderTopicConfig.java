package com.chensoul.bookstore.order.config;

import com.chensoul.bookstore.order.ApplicationProperties;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaOrderTopicConfig {

    private final ApplicationProperties properties;

    KafkaOrderTopicConfig(ApplicationProperties properties) {
        this.properties = properties;
    }

    @Bean
    public NewTopic newOrderQueue() {
        return TopicBuilder.name(properties.newOrderQueue()).build();
    }

    @Bean
    public NewTopic cancelledOrderQueue() {
        return TopicBuilder.name(properties.cancelledOrderQueue()).build();
    }

    @Bean
    public NewTopic deliveredOrderQueue() {
        return TopicBuilder.name(properties.deliveredOrderQueue()).build();
    }

    @Bean
    public NewTopic errorOrderQueue() {
        return TopicBuilder.name(properties.errorOrderQueue()).build();
    }

    @Bean
    public NewTopic orderEventExchange() {
        return TopicBuilder.name(properties.orderEventExchange()).build();
    }
}
