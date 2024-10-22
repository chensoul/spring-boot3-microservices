package com.chensoul.framework.config.logging;

import ch.qos.logback.classic.LoggerContext;
import com.github.loki4j.logback.AbstractLoki4jEncoder;
import com.github.loki4j.logback.ApacheHttpSender;
import com.github.loki4j.logback.JsonEncoder;
import com.github.loki4j.logback.Loki4jAppender;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Slf4j
@Configuration
@ConditionalOnClass(Loki4jAppender.class)
@EnableConfigurationProperties(Logging.class)
public class LokiConfiguration {

    public LokiConfiguration(Logging logging, Environment environment) {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logging.Loki loki = logging.getLoki();
        if (loki.getUrl() != null) {
            addLokiAppender(context, loki, environment);
        }
    }

    public void addLokiAppender(LoggerContext context, Logging.Loki lokiProperties, Environment environment) {
        log.info("Add Loki4jAppender");

        Loki4jAppender loki4jAppender = new Loki4jAppender();
        loki4jAppender.setContext(context);

        AbstractLoki4jEncoder loki4jEncoder = getLoki4jEncoder(lokiProperties, environment);
        loki4jAppender.setFormat(loki4jEncoder);

        ApacheHttpSender apacheHttpSender = new ApacheHttpSender();
        apacheHttpSender.setUrl(lokiProperties.getUrl());
        loki4jAppender.setHttp(apacheHttpSender);
        loki4jAppender.start();

        context.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME).addAppender(loki4jAppender);
    }

    private AbstractLoki4jEncoder getLoki4jEncoder(Logging.Loki lokiProperties, Environment environment) {
        AbstractLoki4jEncoder loki4jEncoder = new JsonEncoder();
        AbstractLoki4jEncoder.LabelCfg labelCfg = new AbstractLoki4jEncoder.LabelCfg();
        if (StringUtils.isNotBlank(lokiProperties.getLabelPattern())) {
            labelCfg.setPattern(lokiProperties
                    .getLabelPattern()
                    .replace("${appName}", environment.getProperty("spring.application.name")));
        }
        loki4jEncoder.setLabel(labelCfg);

        if (StringUtils.isNotBlank(lokiProperties.getMessagePattern())) {
            AbstractLoki4jEncoder.MessageCfg messageCfg = new AbstractLoki4jEncoder.MessageCfg();
            messageCfg.setPattern(lokiProperties.getMessagePattern());
            loki4jEncoder.setMessage(messageCfg);
        }

        loki4jEncoder.setSortByTime(true);
        return loki4jEncoder;
    }
}
