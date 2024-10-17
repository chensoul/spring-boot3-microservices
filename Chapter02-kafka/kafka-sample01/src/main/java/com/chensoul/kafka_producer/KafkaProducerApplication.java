package com.chensoul.kafka_producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Slf4j
@RequiredArgsConstructor
@SpringBootApplication
public class KafkaProducerApplication {
    private final TaskExecutor exec = new SimpleAsyncTaskExecutor();

    public static void main(String[] args) {
        SpringApplication.run(KafkaProducerApplication.class, args).close();
    }

    @Bean
    public CommonErrorHandler errorHandler(KafkaOperations<Object, Object> template) {
        return new DefaultErrorHandler(
            new DeadLetterPublishingRecoverer(template), new FixedBackOff(1000L, 2));
    }

//    @Bean
//    public RecordMessageConverter converter() {
//        return new JsonMessageConverter();
//    }

    @KafkaListener(id = "quickGroup", topics = "quickstart-events")
    public void listenTopic(String message) {
        log.info("Received from quickstart-events: " + message);
    }

    @KafkaListener(id = "fooGroup", topics = "topic1")
    public void listen(Foo2 foo) {
        log.info("Received from topic1: " + foo);
        if (foo.getFoo().startsWith("fail")) {
            throw new RuntimeException("failed");
        }
        this.exec.execute(() -> System.out.println("Hit Enter to terminate..."));
    }

    @KafkaListener(id = "dltGroup", topics = "topic1.DLT")
    public void dltListen(byte[] in) {
        log.info("Received from topic1.DLT: " + new String(in));
        this.exec.execute(() -> System.out.println("Hit Enter to terminate..."));
    }

    @Bean
    public NewTopic topic() {
        return new NewTopic("topic1", 1, (short) 1);
    }

    @Bean
    public NewTopic dlt() {
        return new NewTopic("topic1.DLT", 1, (short) 1);
    }

    @Bean
    @Profile("default") // Don't run from test(s)
    public ApplicationRunner runner() {
        return args -> {
            System.out.println("Hit Enter to terminate...");
            System.in.read();
        };
    }

}
