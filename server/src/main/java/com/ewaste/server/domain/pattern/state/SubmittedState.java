package com.ewaste.server.domain.pattern.state;

import com.ewaste.server.domain.model.pickup.PickupRequest;
import com.ewaste.server.domain.model.pickup.PickupStatus;

/**
 * Initial state when a pickup request is created by a customer.
 */
public class SubmittedState implements PickupState {

    @Override
    public PickupStatus getStatus() {
        return PickupStatus.SUBMITTED;
    }

    @Override
    public void request(PickupRequest context) {
        context.setState(new RequestedState());
    }

    @Override
    public void cancel(PickupRequest context) {
        context.setState(new CancelledState());
    }
}