package com.chensoul.ecommerce.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@EnableConfigServer
@EnableEurekaServer
@SpringBootApplication
@Controller
public class EurekaApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(EurekaApplication.class, args);

        // 方便调试代码，查看配置文件路径在哪里
        String repoLocation = ctx.getEnvironment().getProperty("spring.cloud.config.server.native.searchLocations");
        LOG.info("Serving configurations from folder: " + repoLocation);
    }

    @RequestMapping("/config")
    public String configHome() {
        return "/index.html";
    }
}

