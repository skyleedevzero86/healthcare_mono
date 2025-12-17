package com.sleekydz86.service.auth.security;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class AsyncEncryptionService {

    @Async
    public CompletableFuture<String> encryptAsync(String plainText, HealthcareEncryptionUtil.KeyType keyType) {
        return CompletableFuture.completedFuture(
            HealthcareEncryptionUtil.encrypt(plainText, keyType)
        );
    }

    @Async
    public CompletableFuture<String> decryptAsync(String encryptedText, HealthcareEncryptionUtil.KeyType keyType) {
        return CompletableFuture.completedFuture(
            HealthcareEncryptionUtil.decrypt(encryptedText, keyType)
        );
    }
}

