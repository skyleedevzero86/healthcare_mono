package com.sleekydz86.service.auth.security;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class KeyRotationService {

    private final Map<String, KeyInfo> keyStore = new ConcurrentHashMap<>();

    public KeyRotationService() {
        initializeKeys();
    }

    private void initializeKeys() {
        String authKey = System.getenv("HEALTHCARE_AUTH_KEY");
        String userKey = System.getenv("HEALTHCARE_USER_KEY");
        String healthKey = System.getenv("HEALTHCARE_HEALTH_KEY");

        if (authKey != null) {
            keyStore.put("AUTH", new KeyInfo(authKey, null, LocalDateTime.now()));
        }
        if (userKey != null) {
            keyStore.put("USER", new KeyInfo(userKey, null, LocalDateTime.now()));
        }
        if (healthKey != null) {
            keyStore.put("HEALTH", new KeyInfo(healthKey, null, LocalDateTime.now()));
        }
    }

    @Scheduled(cron = "0 0 2 * * ?")
    public void rotateKeys() {
        LocalDateTime now = LocalDateTime.now();

        keyStore.forEach((keyName, keyInfo) -> {
            if (keyInfo.getCreatedAt().plusDays(90).isBefore(now)) {
                String newKey = generateNewKey();
                keyInfo.setPreviousKey(keyInfo.getCurrentKey());
                keyInfo.setCurrentKey(newKey);
                keyInfo.setCreatedAt(now);

                scheduleReencryption(keyName, keyInfo.getPreviousKey(), newKey);
            }
        });
    }

    private String generateNewKey() {
        byte[] key = new byte[32];
        new SecureRandom().nextBytes(key);
        return Base64.getEncoder().encodeToString(key);
    }

    private void scheduleReencryption(String keyName, String oldKey, String newKey) {
    }

    public KeyInfo getKeyInfo(String keyName) {
        return keyStore.get(keyName);
    }
}

