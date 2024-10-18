package com.chensoul.config.logging;

import ch.qos.logback.classic.LoggerContext;
import com.chensoul.util.Logging;
import com.chensoul.util.LoggingUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.loki4j.logback.Loki4jAppender;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Copy from jhipster
 */
@Configuration
@EnableConfigurationProperties(Logging.class)
@Import({LoggingConfiguration.ConsoleConfigure.class, LoggingConfiguration.LogstashConfigure.class, LoggingConfiguration.LokiConfigure.class})
public class LoggingConfiguration {
    @Value("${spring.application.name}")
    String appName;
    @Value("${server.port}")
    String serverPort;

    private String getCustomFields(ObjectProvider<BuildProperties> buildProperties, ObjectMapper mapper) throws JsonProcessingException {
        Map<String, String> map = new HashMap<>();
        map.put("app_name", appName);
        map.put("app_port", serverPort);
        buildProperties.ifAvailable(it -> map.put("version", it.getVersion()));
        return mapper.writeValueAsString(map);
    }

    @Configuration
    public class ConsoleConfigure {
        public ConsoleConfigure(Logging logging, ObjectProvider<BuildProperties> buildProperties, ObjectMapper mapper) throws JsonProcessingException {
            LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
            String customFields = getCustomFields(buildProperties, mapper);

            if (logging.isUseJsonFormat()) {
                LoggingUtils.addJsonConsoleAppender(context, customFields);
            }
        }
    }

    @Configuration
    @ConditionalOnClass(name = "ch.qos.logback.classic.LoggerContext")
    public class LogstashConfigure {
        public LogstashConfigure(Logging logging, ObjectProvider<BuildProperties> buildProperties, ObjectMapper mapper) throws JsonProcessingException {
            LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
            String customFields = getCustomFields(buildProperties, mapper);

            Logging.Logstash logstashProperties = logging.getLogstash();
            if (logstashProperties.isEnabled()) {
                LoggingUtils.addLogstashTcpSocketAppender(context, customFields, logstashProperties);
            }
            if (logging.isUseJsonFormat() || logstashProperties.isEnabled()) {
                LoggingUtils.addContextListener(context, customFields, logging);
            }
        }
    }

    @Configuration
    @ConditionalOnClass(Loki4jAppender.class)
    public class LokiConfigure {
        public LokiConfigure(Logging logging, ObjectProvider<BuildProperties> buildProperties, ObjectMapper mapper) throws JsonProcessingException {
            LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
            Logging.Loki loki = logging.getLoki();
            if (loki.isEnabled()) {
                LoggingUtils.addLokiAppender(context, loki);
            }
        }
    }
}
