package com.ewaste.server.domain.pattern.state;

import com.ewaste.server.domain.model.pickup.PickupRequest;
import com.ewaste.server.domain.model.pickup.PickupStatus;

/**
 * State representing that waste items have arrived at a certified recycling facility.
 */
public class DeliveredState implements PickupState {

    @Override
    public PickupStatus getStatus() {
        return PickupStatus.DELIVERED;
    }

    @Override
    public void process(PickupRequest context) {
        context.setState(new ProcessingState());
    }
}