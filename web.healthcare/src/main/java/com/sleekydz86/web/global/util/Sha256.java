package com.sleekydz86.web.global.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class Sha256 {

    private Sha256() {
    }

    public static String encryt(String passwd) throws NoSuchAlgorithmException {
        String hex = "";
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        byte[] bytes = new byte[16];
        random.nextBytes(bytes);
        String salt = new String(Base64.getEncoder().encode(bytes));
        String rawAndSalt = passwd + salt;

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(passwd.getBytes());
        hex = String.format("%064x", new BigInteger(1, md.digest()));

        return hex;
    }

    public static String passwdCheck(String passwd, String salt) throws NoSuchAlgorithmException {
        String hex = "";
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(passwd.getBytes());
        hex = String.format("%064x", new BigInteger(1, md.digest()));

        return passwd;
    }
}
