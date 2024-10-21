package com.chensoul.framework.config.logging;

import ch.qos.logback.classic.LoggerContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import net.logstash.logback.appender.LogstashTcpSocketAppender;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Copy from jhipster
 */
@Configuration
@EnableConfigurationProperties(Logging.class)
@ConditionalOnClass({LogstashTcpSocketAppender.class, ObjectMapper.class})
public class LogstashConfiguration {
    @Value("${spring.application.name}")
    String appName;
    @Value("${server.port}")
    String serverPort;

    public LogstashConfiguration(Logging logging, ObjectProvider<BuildProperties> buildProperties, ObjectMapper mapper) throws JsonProcessingException {
        Map<String, String> map = new HashMap<>();
        map.put("app_name", appName);
        map.put("app_port", serverPort);
        buildProperties.ifAvailable(it -> map.put("version", it.getVersion()));
        String customFields = mapper.writeValueAsString(map);

        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        if (logging.isUseJsonFormat()) {
            LogstashUtils.addJsonConsoleAppender(context, customFields);
        }

        Logging.Logstash logstashProperties = logging.getLogstash();
        if (logstashProperties.isEnabled()) {
            LogstashUtils.addLogstashTcpSocketAppender(context, customFields, logstashProperties);
        }
        if (logging.isUseJsonFormat() || logstashProperties.isEnabled()) {
            LogstashUtils.addContextListener(context, customFields, logging);
        }
    }
}
