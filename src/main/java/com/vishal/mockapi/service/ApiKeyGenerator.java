package com.vishal.mockapi.service;

import java.security.SecureRandom;
import java.util.Base64;

public class ApiKeyGenerator {
    private static final SecureRandom secureRandom = new SecureRandom(); // strong random generator
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder().withoutPadding(); // safe for URLs

    public static String generateApiKey(int bits) {
        byte[] randomBytes = new byte[bits]; // 256-bit key
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }

    public static String generateApiKey() {
        return generateApiKey(32); // default to 256 bits
    }
}
