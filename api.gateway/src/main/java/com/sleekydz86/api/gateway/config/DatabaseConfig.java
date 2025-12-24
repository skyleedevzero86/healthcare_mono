package com.sleekydz86.api.gateway.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConditionalOnProperty(name = "spring.datasource.enabled", havingValue = "true", matchIfMissing = false)
@Import({
    DataSourceAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class,
    FlywayAutoConfiguration.class
})
public class DatabaseConfig {
}

