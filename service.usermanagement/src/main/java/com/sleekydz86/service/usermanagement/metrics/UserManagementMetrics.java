package com.sleekydz86.service.usermanagement.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class UserManagementMetrics {

    private final Counter userListQueries;
    private final Counter userInfoQueries;
    private final Counter userInfoUpdates;
    private final Counter userInfoDeletes;
    private final Counter passwordUpdates;
    private final Timer userListQueryTime;
    private final Timer userInfoQueryTime;
    private final Timer userInfoUpdateTime;
    private final Counter doctorSearches;
    private final Counter parentSearches;
    private final MeterRegistry meterRegistry;

    public UserManagementMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.userListQueries = Counter.builder("usermanagement.user.list.queries")
                .description("Number of user list queries")
                .register(meterRegistry);
        
        this.userInfoQueries = Counter.builder("usermanagement.user.info.queries")
                .description("Number of user info queries")
                .register(meterRegistry);
        
        this.userInfoUpdates = Counter.builder("usermanagement.user.info.updates")
                .description("Number of user info updates")
                .register(meterRegistry);
        
        this.userInfoDeletes = Counter.builder("usermanagement.user.info.deletes")
                .description("Number of user info deletes")
                .register(meterRegistry);
        
        this.passwordUpdates = Counter.builder("usermanagement.password.updates")
                .description("Number of password updates")
                .register(meterRegistry);
        
        this.userListQueryTime = Timer.builder("usermanagement.user.list.query.time")
                .description("Time taken to query user list")
                .register(meterRegistry);
        
        this.userInfoQueryTime = Timer.builder("usermanagement.user.info.query.time")
                .description("Time taken to query user info")
                .register(meterRegistry);
        
        this.userInfoUpdateTime = Timer.builder("usermanagement.user.info.update.time")
                .description("Time taken to update user info")
                .register(meterRegistry);
        
        this.doctorSearches = Counter.builder("usermanagement.doctor.searches")
                .description("Number of doctor searches")
                .register(meterRegistry);
        
        this.parentSearches = Counter.builder("usermanagement.parent.searches")
                .description("Number of parent searches")
                .register(meterRegistry);
    }

    public void incrementUserListQueries() {
        userListQueries.increment();
    }

    public void incrementUserInfoQueries() {
        userInfoQueries.increment();
    }

    public void incrementUserInfoUpdates() {
        userInfoUpdates.increment();
    }

    public void incrementUserInfoDeletes() {
        userInfoDeletes.increment();
    }

    public void incrementPasswordUpdates() {
        passwordUpdates.increment();
    }

    public void recordUserListQueryTime(long time, TimeUnit unit) {
        userListQueryTime.record(time, unit);
    }

    public void recordUserInfoQueryTime(long time, TimeUnit unit) {
        userInfoQueryTime.record(time, unit);
    }

    public void recordUserInfoUpdateTime(long time, TimeUnit unit) {
        userInfoUpdateTime.record(time, unit);
    }

    public Timer.Sample startUserListQueryTimer() {
        return Timer.start(meterRegistry);
    }

    public Timer.Sample startUserInfoQueryTimer() {
        return Timer.start(meterRegistry);
    }

    public Timer.Sample startUserInfoUpdateTimer() {
        return Timer.start(meterRegistry);
    }

    public Timer getUserListQueryTime() {
        return userListQueryTime;
    }

    public Timer getUserInfoQueryTime() {
        return userInfoQueryTime;
    }

    public Timer getUserInfoUpdateTime() {
        return userInfoUpdateTime;
    }

    public void incrementDoctorSearches() {
        doctorSearches.increment();
    }

    public void incrementParentSearches() {
        parentSearches.increment();
    }
}

