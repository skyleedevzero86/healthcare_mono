package com.sleekydz86.web.global.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class StringEncrypt {
    public static String encryptAES(String s, String key) throws Exception {
        String encrypted = null;
        try {
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(1, skeySpec);
            encrypted = byteArrayToHex(cipher.doFinal(s.getBytes()));
            return encrypted;
        } catch (Exception e) {
            throw e;
        }
    }

    public static String decryptAES(String s, String key) throws Exception {
        String decrypted = null;
        try {
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(2, skeySpec);
            decrypted = new String(cipher.doFinal(hexToByteArray(s)));
            return decrypted;
        } catch (Exception e) {
            throw e;
        }
    }

    public static byte[] hexToByteArray(String s) {
        byte[] retValue = null;
        if (s != null && s.length() != 0) {
            retValue = new byte[s.length() / 2];
            for (int i = 0; i < retValue.length; i++)
                retValue[i] = (byte) Integer.parseInt(s.substring(2 * i, 2 * i + 2), 16);
        }
        return retValue;
    }

    public static String byteArrayToHex(byte[] buf) {
        StringBuffer strbuf = new StringBuffer(buf.length * 2);
        for (int i = 0; i < buf.length; i++) {
            if ((buf[i] & 0xFF) < 16)
                strbuf.append("0");
            strbuf.append(Long.toString((buf[i] & 0xFF), 16));
        }
        return strbuf.toString();
    }
}
