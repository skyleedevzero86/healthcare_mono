package com.sleekydz86.service.usermanagement.global.util;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class AES256Util {

    private AES256Util() {
    }

    private static final byte[] KEY = {
            103, 102, 48, 57, 88, 69, 55, 104,
            56, 118, 75, 53, 101, 86, 113, 52,
            70, 87, 85, 97, 104, 54, 80, 113,
            65, 110, 57, 100, 106, 107, 68, 81
    };
    private static final byte[] IV = {
            75, 74, 100, 48, 108, 99, 53, 121,
            111, 117, 99, 55, 115, 49, 57, 50
    };

    public static String encrypt(final String str) {
        try {
            SecretKey secretKey = new SecretKeySpec(KEY, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(IV));
            byte[] encrypted = cipher.doFinal(str.getBytes(StandardCharsets.UTF_8));

            return new String(Base64.getEncoder().encode(encrypted));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException
                | InvalidKeyException | BadPaddingException | IllegalBlockSizeException encE) {
            System.err.println(encE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String decrypt(final String str) {
        try {
            SecretKey secretKey = new SecretKeySpec(KEY, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(IV));
            byte[] decrypted = Base64.getDecoder().decode(str.getBytes(StandardCharsets.UTF_8));

            return new String(cipher.doFinal(decrypted), StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException
                | InvalidKeyException | BadPaddingException | IllegalBlockSizeException decE) {
            System.err.println(decE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}