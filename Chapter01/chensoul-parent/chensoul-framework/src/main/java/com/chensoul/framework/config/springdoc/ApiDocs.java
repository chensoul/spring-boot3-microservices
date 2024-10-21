package com.chensoul.framework.config.springdoc;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "api-docs")
public class ApiDocs {

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
