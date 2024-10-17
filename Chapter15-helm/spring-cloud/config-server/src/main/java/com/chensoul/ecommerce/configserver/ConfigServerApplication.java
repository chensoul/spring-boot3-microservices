package com.chensoul.ecommerce.configserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.context.ConfigurableApplicationContext;

@EnableConfigServer
@SpringBootApplication
public class ConfigServerApplication {
    private static final Logger LOG = LoggerFactory.getLogger(ConfigServerApplication.class);

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(ConfigServerApplication.class, args);

        // 方便调试代码，查看配置文件路径在哪里
        String repoLocation = ctx.getEnvironment().getProperty("spring.cloud.config.server.native.searchLocations");
        LOG.info("Serving configurations from folder: " + repoLocation);
    }
}
