package com.sleekydz86.service.healthcare.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.shardingsphere.driver.api.ShardingSphereDataSourceFactory;
import org.apache.shardingsphere.infra.config.algorithm.AlgorithmConfiguration;
import org.apache.shardingsphere.infra.config.rule.RuleConfiguration;
import org.apache.shardingsphere.sharding.api.config.ShardingRuleConfiguration;
import org.apache.shardingsphere.sharding.api.config.rule.ShardingTableRuleConfiguration;
import org.apache.shardingsphere.sharding.api.config.strategy.sharding.StandardShardingStrategyConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.*;

@Configuration
public class ShardingConfig {

    @Value("${sharding.datasource.ds0.url:jdbc:postgresql://shard0-db:5432/healthcare}")
    private String ds0Url;

    @Value("${sharding.datasource.ds1.url:jdbc:postgresql://shard1-db:5432/healthcare}")
    private String ds1Url;

    @Value("${sharding.datasource.ds2.url:jdbc:postgresql://shard2-db:5432/healthcare}")
    private String ds2Url;

    @Value("${sharding.datasource.ds3.url:jdbc:postgresql://shard3-db:5432/healthcare}")
    private String ds3Url;

    @Value("${sharding.datasource.username:healthcare_user}")
    private String username;

    @Value("${sharding.datasource.password:healthcare_pass}")
    private String password;

    @Value("${sharding.datasource.maximum-pool-size:20}")
    private int maximumPoolSize;

    @Value("${sharding.datasource.minimum-idle:5}")
    private int minimumIdle;

    @Bean
    @Primary
    public DataSource dataSource() throws SQLException {
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();

        ShardingTableRuleConfiguration patientTableRule = new ShardingTableRuleConfiguration("patients", "ds${0..3}.patients");
        StandardShardingStrategyConfiguration patientDatabaseStrategy = new StandardShardingStrategyConfiguration("region_id", "patient-inline");
        patientTableRule.setDatabaseShardingStrategy(patientDatabaseStrategy);
        shardingRuleConfig.getTables().add(patientTableRule);

        ShardingTableRuleConfiguration medicalRecordTableRule = new ShardingTableRuleConfiguration("medical_records", "ds${0..3}.medical_records");
        StandardShardingStrategyConfiguration medicalRecordDatabaseStrategy = new StandardShardingStrategyConfiguration("patient_id", "medical-record-inline");
        medicalRecordTableRule.setDatabaseShardingStrategy(medicalRecordDatabaseStrategy);
        shardingRuleConfig.getTables().add(medicalRecordTableRule);

        ShardingTableRuleConfiguration appointmentTableRule = new ShardingTableRuleConfiguration("appointments", "ds${0..3}.appointments");
        StandardShardingStrategyConfiguration appointmentDatabaseStrategy = new StandardShardingStrategyConfiguration("date_hash", "appointment-inline");
        appointmentTableRule.setDatabaseShardingStrategy(appointmentDatabaseStrategy);
        shardingRuleConfig.getTables().add(appointmentTableRule);

        Map<String, DataSource> dataSourceMap = new HashMap<>();
        dataSourceMap.put("ds0", createDataSource(ds0Url));
        dataSourceMap.put("ds1", createDataSource(ds1Url));
        dataSourceMap.put("ds2", createDataSource(ds2Url));
        dataSourceMap.put("ds3", createDataSource(ds3Url));

        Map<String, AlgorithmConfiguration> shardingAlgorithms = new HashMap<>();
        Properties patientProps = new Properties();
        patientProps.setProperty("algorithm-expression", "ds${Math.abs(region_id.hashCode()) % 4}");
        shardingAlgorithms.put("patient-inline", new AlgorithmConfiguration("INLINE", patientProps));

        Properties medicalRecordProps = new Properties();
        medicalRecordProps.setProperty("algorithm-expression", "ds${patient_id % 4}");
        shardingAlgorithms.put("medical-record-inline", new AlgorithmConfiguration("INLINE", medicalRecordProps));

        Properties appointmentProps = new Properties();
        appointmentProps.setProperty("algorithm-expression", "ds${date_hash % 4}");
        shardingAlgorithms.put("appointment-inline", new AlgorithmConfiguration("INLINE", appointmentProps));

        shardingRuleConfig.setShardingAlgorithms(shardingAlgorithms);

        Properties props = new Properties();
        props.setProperty("sql-show", "true");

        Collection<RuleConfiguration> ruleConfigs = Collections.singletonList(shardingRuleConfig);
        return ShardingSphereDataSourceFactory.createDataSource(dataSourceMap, ruleConfigs, props);
    }

    private DataSource createDataSource(String url) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(maximumPoolSize);
        config.setMinimumIdle(minimumIdle);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        return new HikariDataSource(config);
    }
}

