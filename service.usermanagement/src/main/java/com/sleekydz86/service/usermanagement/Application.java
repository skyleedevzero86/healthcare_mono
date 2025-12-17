package com.sleekydz86.service.usermanagement;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Slf4j
@EnableDiscoveryClient
@SpringBootApplication
@EntityScan("com.sleekydz86.service.usermanagement.entity")
@EnableJpaRepositories("com.sleekydz86.service.usermanagement.repository")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
