package com.chensoul.framework.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("application")
public class ApplicationProperties {

    @NestedConfigurationProperty
    private Cors cors = new Cors();

    @NestedConfigurationProperty
    private Logging logging = new Logging();

    @NestedConfigurationProperty
    private ApiDocs apiDocs = new ApiDocs();

    @Data
    public static class Cors {
        private String pathPattern = "/api/**";
        private String allowedMethods = "*";
        private String allowedHeaders = "*";
        private String allowedOriginPatterns = "*";
        private boolean allowCredentials = true;
    }

    @Data
    public static class Logging {
        private final Logstash logstash = new Logstash();
        private final Loki loki = new Loki();
        private boolean useJsonFormat = false;

        @Data
        public static class Loki {
            private String url = "http://localhost:3100/loki/api/v1/push";
            private String labelPattern = "application=${appName},host=${HOSTNAME},level=%level";
            private String messagePattern = "%level %logger{36} %thread | %msg %ex";
        }

        @Data
        public static class Logstash {
            private boolean enabled = false;
            private String host = "localhost";
            private int port = 5000;
            private int ringBufferSize = 512;
        }
    }

    @Data
    public static class ApiDocs {

        private String title;

        private String description;

        private String version;

        private String termsOfServiceUrl;

        private String contactName;

        private String contactUrl;

        private String contactEmail;

        private String license = "Apache 2.0";

        private String licenseUrl = "https://www.apache.org/licenses/LICENSE-2.0.txt";

        private String[] defaultIncludePattern;

        private String[] managementIncludePattern;

        private Server[] servers = {};

        @Data
        public static class Server {
            private String url;
            private String description;
        }
    }
}
