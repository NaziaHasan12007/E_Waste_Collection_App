package com.ewaste.server.domain.pattern.state;

import com.ewaste.server.domain.model.pickup.PickupRequest;
import com.ewaste.server.domain.model.pickup.PickupStatus;

/**
 * State indicating a collector has been dispatched and scheduled.
 */
public class AssignedState implements PickupState {

    @Override
    public PickupStatus getStatus() {
        return PickupStatus.ASSIGNED;
    }

    @Override
    public void assign(PickupRequest context, Long collectorId) {
        if (collectorId == null || collectorId <= 0) {
            throw new IllegalArgumentException("A valid collector ID must be provided to assign a pickup.");
        }
        context.setCollectorId(collectorId);
    }

    @Override
    public void collect(PickupRequest context) {
        context.setState(new CollectedState());
    }

    @Override
    public void cancel(PickupRequest context) {
        context.setCollectorId(null);
        context.setState(new CancelledState());
    }
}