package com.ewaste.server.domain.pattern.state;

import com.ewaste.server.domain.model.pickup.PickupStatus;

/**
 * Terminal state indicating the pickup lifecycle has completed successfully,
 * yields are archived, and rewards are issued.
 */
public class CompletedState implements PickupState {

    @Override
    public PickupStatus getStatus() {
        return PickupStatus.COMPLETED;
    }
}