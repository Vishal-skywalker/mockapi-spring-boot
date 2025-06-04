package com.vishal.mockapi.service;

import java.security.SecureRandom;
import java.util.Base64;

public class ApiKeyGenerator {
    private static final SecureRandom secureRandom = new SecureRandom(); // strong random generator
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder().withoutPadding(); // safe for URLs

    public static String generateApiKey() {
        byte[] randomBytes = new byte[32]; // 256-bit key
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }
}
