package com.example.logapp.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public final class CryptoUtil {
    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    private CryptoUtil() {}

    public static String bcrypt(String plain) {
        return ENCODER.encode(plain);
    }

    public static boolean matches(String plain, String hash) {
        return ENCODER.matches(plain, hash);
    }
}
