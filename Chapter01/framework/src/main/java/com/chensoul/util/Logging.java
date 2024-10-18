package com.chensoul.util;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "logging")
public class Logging {

    private final Logstash logstash = new Logstash();
    private final Loki loki = new Loki();

    private boolean useJsonFormat = false;

    public boolean isUseJsonFormat() {
        return useJsonFormat;
    }

    public void setUseJsonFormat(boolean useJsonFormat) {
        this.useJsonFormat = useJsonFormat;
    }

    public Logstash getLogstash() {
        return logstash;
    }

    @Data
    public class Loki {

        private boolean enabled = false;

        private String url = "http://localhost:3100/loki/api/v1/push";

        private String labelPattern = "application=${appName},host=${HOSTNAME},level=%level";

        private String messagePattern = "${FILE_LOG_PATTERN}";
    }

    @Data
    public class Logstash {

        private boolean enabled = false;

        private String host = "localhost";

        private int port = 5000;

        private int ringBufferSize = 512;
    }
}
