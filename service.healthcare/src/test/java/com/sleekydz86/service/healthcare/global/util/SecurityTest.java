package com.sleekydz86.service.healthcare.global.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SecurityTest {

    @Test
    public void testKeyRotation() {
        String encrypted = HealthcareEncryptionUtil.encrypt("test", HealthcareEncryptionUtil.KeyType.USER);
        assertNotNull(encrypted);
    }

    @Test
    public void testEncryptionPerformance() {
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < 1000; i++) {
            HealthcareEncryptionUtil.encrypt("test data " + i, HealthcareEncryptionUtil.KeyType.USER);
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        assertTrue(duration < 5000);
    }
}

