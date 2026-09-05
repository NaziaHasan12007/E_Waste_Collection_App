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
    public void collect(PickupRequest context) {
        context.setState(new CollectedState());
    }

    @Override
    public void cancel(PickupRequest context) {
        context.setCollectorId(null);
        context.setState(new CancelledState());
    }
}