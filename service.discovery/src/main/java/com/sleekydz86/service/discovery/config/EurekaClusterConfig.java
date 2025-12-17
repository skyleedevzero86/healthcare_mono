package com.sleekydz86.service.discovery.config;

import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.cloud.netflix.eureka.server.EurekaServerConfigBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EurekaClusterConfig {

    @Bean
    public EurekaInstanceConfigBean eurekaInstanceConfig() {
        EurekaInstanceConfigBean config = new EurekaInstanceConfigBean();
        String hostname = System.getenv("HOSTNAME");
        if (hostname == null || hostname.isEmpty()) {
            hostname = System.getProperty("eureka.instance.hostname", "localhost");
        }
        config.setHostname(hostname);
        
        String podIp = System.getenv("POD_IP");
        if (podIp != null && !podIp.isEmpty()) {
            config.setIpAddress(podIp);
        }
        
        config.setNonSecurePort(8761);
        config.setSecurePort(8762);
        return config;
    }

    @Bean
    public EurekaServerConfigBean eurekaServerConfig() {
        EurekaServerConfigBean config = new EurekaServerConfigBean();
        config.setEnableSelfPreservation(false);
        config.setEvictionIntervalTimerInMs(5000);
        config.setResponseCacheUpdateIntervalMs(5000);
        return config;
    }
}

