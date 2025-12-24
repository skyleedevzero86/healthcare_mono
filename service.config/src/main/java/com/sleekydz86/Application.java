package com.sleekydz86;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class Application {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Application.class);
        app.setDefaultProperties(java.util.Map.of("spring.profiles.active", "native"));
        app.run(args);
    }

}
