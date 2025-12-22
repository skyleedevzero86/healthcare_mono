package com.sleekydz86.service.healthcare.config;

import com.sleekydz86.service.healthcare.client.AuthServiceClient;
import com.sleekydz86.service.healthcare.event.EventPublisher;
import com.sleekydz86.service.healthcare.eventsourcing.EventStore;
import com.sleekydz86.service.healthcare.global.mapper.HealthcareMapper;
import com.sleekydz86.service.healthcare.metrics.HealthcareMetrics;
import com.sleekydz86.service.healthcare.repository.*;
import com.sleekydz86.service.healthcare.service.ai.AIResponseService;
import com.sleekydz86.service.healthcare.service.ai.AIResponseServiceImpl;
import com.sleekydz86.service.healthcare.service.ai.AIService;
import com.sleekydz86.service.healthcare.service.ai.AIServiceImpl;
import com.sleekydz86.service.healthcare.service.chart.ChartDataProcessor;
import com.sleekydz86.service.healthcare.service.chart.ChartDataProcessorImpl;
import com.sleekydz86.service.healthcare.service.chart.ChartDataService;
import com.sleekydz86.service.healthcare.service.chart.ChartDataServiceImpl;
import com.sleekydz86.service.healthcare.service.community.CommunityService;
import com.sleekydz86.service.healthcare.service.community.CommunityServiceImpl;
import com.sleekydz86.service.healthcare.service.healthdata.HealthDataService;
import com.sleekydz86.service.healthcare.service.healthdata.HealthDataServiceImpl;
import com.sleekydz86.service.healthcare.service.score.HealthScoreService;
import com.sleekydz86.service.healthcare.service.score.HealthScoreServiceImpl;
import com.sleekydz86.service.healthcare.service.score.ScoreCalculator;
import com.sleekydz86.service.healthcare.service.score.ScoreCalculatorImpl;
import com.sleekydz86.service.healthcare.strategy.DataProcessingService;
import com.sleekydz86.service.healthcare.strategy.DataProcessor;
import com.sleekydz86.service.healthcare.validation.HealthDataValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ServiceConfiguration {

    @Bean
    public HealthDataRepository healthDataRepository(HealthcareMapper healthcareMapper) {
        return new HealthDataRepositoryImpl(healthcareMapper);
    }

    @Bean
    public HealthDataValidator healthDataValidator() {
        return new HealthDataValidator();
    }

    @Bean
    public HealthDataService healthDataService(HealthDataRepository healthDataRepository,
                                             HealthDataValidator healthDataValidator,
                                             DataProcessingService dataProcessingService,
                                             EventPublisher eventPublisher,
                                             EventStore eventStore,
                                             AuthServiceClient authServiceClient,
                                             HealthcareMetrics healthcareMetrics,
                                             io.micrometer.core.instrument.MeterRegistry meterRegistry) {
        return new HealthDataServiceImpl(healthDataRepository, healthDataValidator, dataProcessingService,
                eventPublisher, eventStore, authServiceClient, healthcareMetrics, meterRegistry);
    }

    @Bean
    public ChartDataRepository chartDataRepository(HealthcareMapper healthcareMapper) {
        return new ChartDataRepositoryImpl(healthcareMapper);
    }

    @Bean
    public ChartDataProcessor chartDataProcessor() {
        return new ChartDataProcessorImpl();
    }

    @Bean
    public ChartDataService chartDataService(ChartDataRepository chartDataRepository) {
        return new ChartDataServiceImpl(chartDataRepository);
    }

    @Bean
    public HealthScoreRepository healthScoreRepository(HealthcareMapper healthcareMapper) {
        return new HealthScoreRepositoryImpl(healthcareMapper);
    }

    @Bean
    public ScoreCalculator scoreCalculator(HealthScoreRepository healthScoreRepository) {
        return new ScoreCalculatorImpl(healthScoreRepository);
    }

    @Bean
    public HealthScoreService healthScoreService(HealthScoreRepository healthScoreRepository,
                                                HealthDataValidator healthDataValidator,
                                                AuthServiceClient authServiceClient,
                                                HealthcareMetrics healthcareMetrics) {
        return new HealthScoreServiceImpl(healthScoreRepository, healthDataValidator, authServiceClient, healthcareMetrics);
    }

    @Bean
    public AIResponseRepository aiResponseRepository(HealthcareMapper healthcareMapper) {
        return new AIResponseRepositoryImpl(healthcareMapper);
    }

    @Bean
    public AIService aiService() {
        return new AIServiceImpl();
    }

    @Bean
    public AIResponseService aiResponseService(AIResponseRepository aiResponseRepository,
                                             AIService aiService,
                                             HealthDataValidator healthDataValidator,
                                             AuthServiceClient authServiceClient) {
        return new AIResponseServiceImpl(aiResponseRepository, aiService, healthDataValidator, authServiceClient);
    }

    @Bean
    public CommunityRepository communityRepository(HealthcareMapper healthcareMapper) {
        return new CommunityRepositoryImpl(healthcareMapper);
    }

    @Bean
    public CommunityService communityService(CommunityRepository communityRepository,
                                           HealthDataValidator healthDataValidator,
                                           AuthServiceClient authServiceClient,
                                           HealthcareMetrics healthcareMetrics) {
        return new CommunityServiceImpl(communityRepository, healthDataValidator, authServiceClient, healthcareMetrics);
    }

    @Bean
    public DataProcessingService dataProcessingService(List<DataProcessor> processors) {
        return new DataProcessingService(processors);
    }
}

