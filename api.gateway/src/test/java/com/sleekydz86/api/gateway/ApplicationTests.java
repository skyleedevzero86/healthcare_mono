package com.sleekydz86.api.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.config.import=optional:configserver:",
    "spring.cloud.config.enabled=false"
})
class ApplicationTests {

    @Test
    void contextLoads() {
    }

}
