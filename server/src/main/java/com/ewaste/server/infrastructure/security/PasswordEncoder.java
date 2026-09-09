package com.ewaste.server.infrastructure.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Encapsulates cryptographic password hashing using BCrypt.
 */
@Component
public class PasswordEncoder {

    private final BCryptPasswordEncoder delegate = new BCryptPasswordEncoder(12);

    public String encode(CharSequence rawPassword) {
        if (rawPassword == null) {
            throw new IllegalArgumentException("Raw password cannot be null");
        }
        return delegate.encode(rawPassword);
    }

    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }
        return delegate.matches(rawPassword, encodedPassword);
    }
}