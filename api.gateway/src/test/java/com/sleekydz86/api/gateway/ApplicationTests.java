package com.sleekydz86.api.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(properties = {
    "spring.cloud.config.enabled=false",
    "spring.config.import="
})
class ApplicationTests {

    @Test
    void contextLoads() {
    }

}
