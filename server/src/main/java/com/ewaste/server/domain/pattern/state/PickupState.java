package com.ewaste.server.domain.pattern.state;

import com.ewaste.server.common.exception.InvalidPickupStateException;
import com.ewaste.server.domain.model.pickup.PickupRequest;
import com.ewaste.server.domain.model.pickup.PickupStatus;

/**
 * State pattern interface defining allowable operations and lifecycle
 * transitions for a pickup request aggregate root.
 */
public interface PickupState {

    PickupStatus getStatus();

    default void request(PickupRequest context) {
        throw new InvalidPickupStateException(getStatus().name(), "request");
    }

    default void assign(PickupRequest context, Long collectorId) {
        throw new InvalidPickupStateException(getStatus().name(), "assign");
    }

    default void collect(PickupRequest context) {
        throw new InvalidPickupStateException(getStatus().name(), "collect");
    }

    default void deliver(PickupRequest context, Long centerId) {
        throw new InvalidPickupStateException(getStatus().name(), "deliver");
    }

    default void process(PickupRequest context) {
        throw new InvalidPickupStateException(getStatus().name(), "process");
    }

    default void complete(PickupRequest context) {
        throw new InvalidPickupStateException(getStatus().name(), "complete");
    }

    default void cancel(PickupRequest context) {
        throw new InvalidPickupStateException(getStatus().name(), "cancel");
    }
}