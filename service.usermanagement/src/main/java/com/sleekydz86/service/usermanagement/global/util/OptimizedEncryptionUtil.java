package com.sleekydz86.service.usermanagement.global.util;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class OptimizedEncryptionUtil {

    @Cacheable(value = "encryptionCache", key = "#plainText + #keyType")
    public String encryptWithCache(String plainText, HealthcareEncryptionUtil.KeyType keyType) {
        return HealthcareEncryptionUtil.encrypt(plainText, keyType);
    }

    @Cacheable(value = "decryptionCache", key = "#encryptedText + #keyType")
    public String decryptWithCache(String encryptedText, HealthcareEncryptionUtil.KeyType keyType) {
        return HealthcareEncryptionUtil.decrypt(encryptedText, keyType);
    }
}

