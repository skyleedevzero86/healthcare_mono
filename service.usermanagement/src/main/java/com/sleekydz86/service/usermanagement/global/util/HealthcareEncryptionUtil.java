package com.sleekydz86.service.usermanagement.global.util;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class HealthcareEncryptionUtil {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 16;

    private static final String AUTH_KEY = System.getenv("HEALTHCARE_AUTH_KEY");
    private static final String USER_KEY = System.getenv("HEALTHCARE_USER_KEY");
    private static final String HEALTH_KEY = System.getenv("HEALTHCARE_HEALTH_KEY");

    public enum KeyType {
        AUTH, USER, HEALTH, DEFAULT
    }

    public static String encrypt(String plainText, KeyType keyType) {
        try {
            String key = getKeyByType(keyType);
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);

            byte[] iv = new byte[GCM_IV_LENGTH];
            new SecureRandom().nextBytes(iv);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);

            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);
            byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            byte[] encryptedWithIv = new byte[GCM_IV_LENGTH + cipherText.length];
            System.arraycopy(iv, 0, encryptedWithIv, 0, GCM_IV_LENGTH);
            System.arraycopy(cipherText, 0, encryptedWithIv, GCM_IV_LENGTH, cipherText.length);

            return Base64.getEncoder().encodeToString(encryptedWithIv);
        } catch (Exception e) {
            throw new RuntimeException("암호화 실패", e);
        }
    }

    public static String decrypt(String encryptedText, KeyType keyType) {
        try {
            String key = getKeyByType(keyType);
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);

            byte[] encryptedWithIv = Base64.getDecoder().decode(encryptedText);
            byte[] iv = new byte[GCM_IV_LENGTH];
            System.arraycopy(encryptedWithIv, 0, iv, 0, GCM_IV_LENGTH);

            byte[] cipherText = new byte[encryptedWithIv.length - GCM_IV_LENGTH];
            System.arraycopy(encryptedWithIv, GCM_IV_LENGTH, cipherText, 0, cipherText.length);

            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

            byte[] plainText = cipher.doFinal(cipherText);
            return new String(plainText, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("복호화 실패", e);
        }
    }

    private static String getKeyByType(KeyType keyType) {
        String key;
        switch (keyType) {
            case AUTH:
                key = AUTH_KEY != null ? AUTH_KEY : "default_auth_key_32_chars_long_2025";
                break;
            case USER:
                key = USER_KEY != null ? USER_KEY : "default_user_key_32_chars_long_2025";
                break;
            case HEALTH:
                key = HEALTH_KEY != null ? HEALTH_KEY : "default_health_key_32_chars_long_2025";
                break;
            default:
                key = "default_encryption_key_32_chars_long_2025";
                break;
        }
        return normalizeKey(key, 32);
    }

    private static String normalizeKey(String key, int targetLength) {
        if (key == null) {
            key = "";
        }
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length == targetLength) {
            return key;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(keyBytes);
            byte[] normalized = new byte[targetLength];
            System.arraycopy(hash, 0, normalized, 0, targetLength);
            return new String(normalized, StandardCharsets.UTF_8);
        } catch (Exception e) {
            byte[] normalized = new byte[targetLength];
            for (int i = 0; i < targetLength; i++) {
                normalized[i] = keyBytes[i % keyBytes.length];
            }
            return new String(normalized, StandardCharsets.UTF_8);
        }
    }

    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update((password + salt).getBytes());
            byte[] hashedBytes = md.digest();
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (Exception e) {
            throw new RuntimeException("해시 생성 실패", e);
        }
    }

    public static String generateSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
}
