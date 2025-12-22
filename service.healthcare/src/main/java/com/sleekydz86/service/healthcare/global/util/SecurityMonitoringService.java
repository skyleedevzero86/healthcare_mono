package com.sleekydz86.service.healthcare.global.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class SecurityMonitoringService {

    private static final Logger logger = LoggerFactory.getLogger(SecurityMonitoringService.class);

    private final Map<String, AtomicLong> encryptionCounters = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> decryptionCounters = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> failureCounters = new ConcurrentHashMap<>();

    public void recordEncryption(String dataType) {
        encryptionCounters.computeIfAbsent(dataType, k -> new AtomicLong(0)).incrementAndGet();
    }

    public void recordDecryption(String dataType) {
        decryptionCounters.computeIfAbsent(dataType, k -> new AtomicLong(0)).incrementAndGet();
    }

    public void recordFailure(String operation) {
        failureCounters.computeIfAbsent(operation, k -> new AtomicLong(0)).incrementAndGet();
    }

    @Scheduled(fixedRate = 300000)
    public void generateSecurityReport() {
        logger.info("=== 보안 모니터링 리포트 ===");
        logger.info("암호화 작업: {}", encryptionCounters);
        logger.info("복호화 작업: {}", decryptionCounters);
        logger.info("실패 작업: {}", failureCounters);

        failureCounters.forEach((operation, count) -> {
            if (count.get() > 100) {
                logger.error("작업에 대한 높은 실패율 감지: {} ({} 실패)",
                        operation, count.get());
                sendSecurityAlert(operation, count.get());
            }
        });
    }

    private void sendSecurityAlert(String operation, long failureCount) {
        logger.error("보안 경고: {}에 대한 높은 실패율 - {} 실패",
                operation, failureCount);
    }
}
