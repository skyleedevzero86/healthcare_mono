package com.sleekydz86.service.auth.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class Sha256 {

    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private Sha256() {
    }

    public static String encryt(String passwd) {
        return passwordEncoder.encode(passwd);
    }

    public static String passwdCheck(String passwd, String encodedPassword) {
        return passwordEncoder.matches(passwd, encodedPassword) ? encodedPassword : null;
    }
}
