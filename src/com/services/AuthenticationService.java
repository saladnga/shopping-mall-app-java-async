package com.services;

import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;

public class AuthenticationService {
    private static final Pbkdf2PasswordEncoder encoder = new Pbkdf2PasswordEncoder(
            "", // secret
            16, // salt length (bytes)
            310000, // iterations (Spring recommended default)
            Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256);

    public String hashPassword(String password) {
        if (password == null)
            return null;
        return encoder.encode(password);
    }

    public boolean verifyPassword(String raw, String encoded) {
        if (raw == null || encoded == null)
            return false;
        return encoder.matches(raw, encoded);
    }
}