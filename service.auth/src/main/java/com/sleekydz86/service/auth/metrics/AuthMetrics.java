package com.sleekydz86.service.auth.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class AuthMetrics {

    private final Counter signinAttempts;
    private final Counter signinSuccess;
    private final Counter signinFailure;
    private final Counter signupAttempts;
    private final Counter signupSuccess;
    private final Timer signinProcessingTime;
    private final Timer signupProcessingTime;
    private final Counter tokenGenerated;
    private final Counter tokenRefreshed;
    private final Counter logoutCount;
    private final MeterRegistry meterRegistry;

    public AuthMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.signinAttempts = Counter.builder("auth.signin.attempts")
                .description("Number of signin attempts")
                .register(meterRegistry);

        this.signinSuccess = Counter.builder("auth.signin.success")
                .description("Number of successful signins")
                .tag("status", "success")
                .register(meterRegistry);

        this.signinFailure = Counter.builder("auth.signin.failure")
                .description("Number of failed signins")
                .tag("status", "failure")
                .register(meterRegistry);

        this.signupAttempts = Counter.builder("auth.signup.attempts")
                .description("Number of signup attempts")
                .register(meterRegistry);

        this.signupSuccess = Counter.builder("auth.signup.success")
                .description("Number of successful signups")
                .register(meterRegistry);

        this.signinProcessingTime = Timer.builder("auth.signin.processing.time")
                .description("Time taken to process signin")
                .register(meterRegistry);

        this.signupProcessingTime = Timer.builder("auth.signup.processing.time")
                .description("Time taken to process signup")
                .register(meterRegistry);

        this.tokenGenerated = Counter.builder("auth.token.generated")
                .description("Number of tokens generated")
                .register(meterRegistry);

        this.tokenRefreshed = Counter.builder("auth.token.refreshed")
                .description("Number of tokens refreshed")
                .register(meterRegistry);

        this.logoutCount = Counter.builder("auth.logout.count")
                .description("Number of logout operations")
                .register(meterRegistry);
    }

    public void incrementSigninAttempts() {
        signinAttempts.increment();
    }

    public void incrementSigninSuccess() {
        signinSuccess.increment();
    }

    public void incrementSigninFailure() {
        signinFailure.increment();
    }

    public void incrementSignupAttempts() {
        signupAttempts.increment();
    }

    public void incrementSignupSuccess() {
        signupSuccess.increment();
    }

    public void recordSigninProcessingTime(long time, TimeUnit unit) {
        signinProcessingTime.record(time, unit);
    }

    public void recordSignupProcessingTime(long time, TimeUnit unit) {
        signupProcessingTime.record(time, unit);
    }

    public Timer.Sample startSigninProcessingTimer() {
        return Timer.start(meterRegistry);
    }

    public Timer.Sample startSignupProcessingTimer() {
        return Timer.start(meterRegistry);
    }

    public Timer getSigninProcessingTime() {
        return signinProcessingTime;
    }

    public Timer getSignupProcessingTime() {
        return signupProcessingTime;
    }

    public void incrementTokenGenerated() {
        tokenGenerated.increment();
    }

    public void incrementTokenRefreshed() {
        tokenRefreshed.increment();
    }

    public void incrementLogoutCount() {
        logoutCount.increment();
    }
}
