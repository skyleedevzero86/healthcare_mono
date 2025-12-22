package com.sleekydz86.service.healthcare.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class HealthcareMetrics {

    private final Counter healthDataProcessed;
    private final Counter healthDataProcessedMinute;
    private final Counter healthDataProcessedDaily;
    private final Timer healthDataProcessingTime;
    private final Timer healthInfoQueryTime;
    private final Counter healthScoreCalculated;
    private final Counter communityPostCreated;
    private final MeterRegistry meterRegistry;

    public HealthcareMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;

        this.healthDataProcessed = Counter.builder("healthcare.data.processed")
                .description("Number of health data processed")
                .register(meterRegistry);

        this.healthDataProcessedMinute = Counter.builder("healthcare.data.processed.minute")
                .description("Number of minute health data processed")
                .tag("type", "minute")
                .register(meterRegistry);

        this.healthDataProcessedDaily = Counter.builder("healthcare.data.processed.daily")
                .description("Number of daily health data processed")
                .tag("type", "daily")
                .register(meterRegistry);

        this.healthDataProcessingTime = Timer.builder("healthcare.data.processing.time")
                .description("Time taken to process health data")
                .register(meterRegistry);

        this.healthInfoQueryTime = Timer.builder("healthcare.info.query.time")
                .description("Time taken to query health information")
                .register(meterRegistry);

        this.healthScoreCalculated = Counter.builder("healthcare.score.calculated")
                .description("Number of health scores calculated")
                .register(meterRegistry);

        this.communityPostCreated = Counter.builder("healthcare.community.post.created")
                .description("Number of community posts created")
                .register(meterRegistry);
    }

    public void incrementHealthDataProcessed() {
        healthDataProcessed.increment();
    }

    public void incrementHealthDataProcessedMinute() {
        healthDataProcessedMinute.increment();
    }

    public void incrementHealthDataProcessedDaily() {
        healthDataProcessedDaily.increment();
    }

    public void recordHealthDataProcessingTime(long time, TimeUnit unit) {
        healthDataProcessingTime.record(time, unit);
    }

    public void recordHealthInfoQueryTime(long time, TimeUnit unit) {
        healthInfoQueryTime.record(time, unit);
    }

    public Timer.Sample startHealthDataProcessingTimer() {
        return Timer.start(meterRegistry);
    }

    public Timer.Sample startHealthInfoQueryTimer() {
        return Timer.start(meterRegistry);
    }

    public void incrementHealthScoreCalculated() {
        healthScoreCalculated.increment();
    }

    public void incrementCommunityPostCreated() {
        communityPostCreated.increment();
    }

    public Timer getHealthDataProcessingTime() {
        return healthDataProcessingTime;
    }

    public Timer getHealthInfoQueryTime() {
        return healthInfoQueryTime;
    }
}
