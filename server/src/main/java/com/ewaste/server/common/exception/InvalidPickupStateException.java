package com.ewaste.server.common.exception;

/**
 * Domain exception thrown when an illegal state transition is attempted
 * on a pickup request lifecycle.
 */
public class InvalidPickupStateException extends BusinessRuleException {

    public InvalidPickupStateException(String message) {
        super(message);
    }

    public InvalidPickupStateException(String currentState, String attemptedAction) {
        super(String.format("Cannot perform operation '%s' while pickup is in '%s' state.", attemptedAction, currentState));
    }
}