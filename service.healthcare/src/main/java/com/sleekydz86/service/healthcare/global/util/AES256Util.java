package com.sleekydz86.service.healthcare.global.util;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Slf4j
public class AES256Util {

    private AES256Util() {
    }

    private static byte[] getKey() {
        String keyEnv = System.getenv("ENCRYPTION_AES256_KEY");
        if (keyEnv == null || keyEnv.isEmpty()) {
            keyEnv = System.getProperty("encryption.aes256.key");
        }
        if (keyEnv == null || keyEnv.isEmpty()) {
            throw new IllegalStateException("AES256 encryption key is not configured. Set ENCRYPTION_AES256_KEY environment variable or encryption.aes256.key system property.");
        }
        if (keyEnv.length() != 32) {
            throw new IllegalStateException("AES256 encryption key must be 32 bytes (256 bits).");
        }
        return keyEnv.getBytes(StandardCharsets.UTF_8);
    }

    private static byte[] getIv() {
        String ivEnv = System.getenv("ENCRYPTION_AES256_IV");
        if (ivEnv == null || ivEnv.isEmpty()) {
            ivEnv = System.getProperty("encryption.aes256.iv");
        }
        if (ivEnv == null || ivEnv.isEmpty()) {
            throw new IllegalStateException("AES256 encryption IV is not configured. Set ENCRYPTION_AES256_IV environment variable or encryption.aes256.iv system property.");
        }
        if (ivEnv.length() != 16) {
            throw new IllegalStateException("AES256 encryption IV must be 16 bytes (128 bits).");
        }
        return ivEnv.getBytes(StandardCharsets.UTF_8);
    }

    public static String encrypt(final String str) {
        try {
            byte[] keyBytes = getKey();
            byte[] ivBytes = getIv();
            SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(ivBytes));
            byte[] encrypted = cipher.doFinal(str.getBytes(StandardCharsets.UTF_8));

            return new String(Base64.getEncoder().encode(encrypted));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException
                | InvalidKeyException | BadPaddingException | IllegalBlockSizeException encE) {
            log.error("AES256 암호화 오류", encE);
        } catch (Exception e) {
            log.error("AES256 암호화 중 예상치 못한 오류", e);
        }
        return "";
    }

    public static String decrypt(final String str) {
        try {
            byte[] keyBytes = getKey();
            byte[] ivBytes = getIv();
            SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(ivBytes));
            byte[] decrypted = Base64.getDecoder().decode(str.getBytes(StandardCharsets.UTF_8));

            return new String(cipher.doFinal(decrypted), StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException
                | InvalidKeyException | BadPaddingException | IllegalBlockSizeException decE) {
            log.error("AES256 복호화 오류", decE);
        } catch (Exception e) {
            log.error("AES256 복호화 중 예상치 못한 오류", e);
        }
        return "";
    }
}
