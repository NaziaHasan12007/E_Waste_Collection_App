package com.ewaste.server.common.validation;

import com.ewaste.server.common.exception.BusinessRuleException;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class UserValidator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"
    );

    public void validateCredentials(String email, String password) {
        validateEmail(email);
        validatePassword(password);
    }

    public void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new BusinessRuleException("Email address cannot be empty.");
        }
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new BusinessRuleException("Email format is invalid: " + email);
        }
    }

    public void validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new BusinessRuleException("Password cannot be empty.");
        }
        if (password.length() < 6) {
            throw new BusinessRuleException("Password must be at least 6 characters long.");
        }
    }

    public void validateFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new BusinessRuleException("Full name cannot be blank.");
        }
        if (fullName.trim().length() < 2) {
            throw new BusinessRuleException("Full name must contain at least 2 characters.");
        }
    }
}