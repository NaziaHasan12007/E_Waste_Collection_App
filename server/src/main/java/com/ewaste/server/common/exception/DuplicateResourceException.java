package com.ewaste.server.common.exception;

/**
 * Thrown when an entity creation violates unique constraints (e.g., duplicate email address).
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }

    public DuplicateResourceException(String resourceName, String field, String value) {
        super(String.format("%s with %s '%s' already exists.", resourceName, field, value));
    }
}