package com.ewaste.server.common.exception;

/**
 * Thrown when an actor lacks sufficient credentials or role privileges for a requested action.
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}