package com.ewaste.server.common.exception;

/**
 * Thrown when an operation violates domain business invariants or constraints.
 */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }

    public BusinessRuleException(String message, Throwable cause) {
        super(message, cause);
    }
}