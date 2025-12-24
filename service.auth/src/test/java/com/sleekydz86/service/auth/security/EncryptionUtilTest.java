package com.sleekydz86.service.auth.security;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
    "spring.cloud.config.enabled=false",
    "spring.cloud.discovery.enabled=false"
})
@TestPropertySource(properties = {
    "spring.cloud.config.fail-fast=false"
})
public class EncryptionUtilTest {

    @Test
    public void testEncryptionDecryption() {
        String plainText = "test data";
        String encrypted = HealthcareEncryptionUtil.encrypt(plainText, HealthcareEncryptionUtil.KeyType.USER);
        String decrypted = HealthcareEncryptionUtil.decrypt(encrypted, HealthcareEncryptionUtil.KeyType.USER);

        assertEquals(plainText, decrypted);
        assertNotEquals(plainText, encrypted);
    }

    @Test
    public void testPasswordHashing() {
        String password = "testPassword123";
        String salt = HealthcareEncryptionUtil.generateSalt();
        String hashed = HealthcareEncryptionUtil.hashPassword(password, salt);

        assertNotNull(hashed);
        assertNotEquals(password, hashed);
        assertTrue(hashed.length() > 0);
    }
}

