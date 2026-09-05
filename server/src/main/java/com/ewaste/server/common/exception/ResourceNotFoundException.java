package com.ewaste.server.common.exception;

/**
 * Thrown when an entity (pickup, user, collector, center) cannot be located in the database.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceName, Long id) {
        super(String.format("%s with ID '%d' was not found.", resourceName, id));
    }

    public ResourceNotFoundException(String resourceName, String identifier) {
        super(String.format("%s with identifier '%s' was not found.", resourceName, identifier));
    }
}