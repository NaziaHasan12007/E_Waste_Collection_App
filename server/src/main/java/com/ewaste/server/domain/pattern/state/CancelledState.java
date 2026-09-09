package com.ewaste.server.domain.pattern.state;

import com.ewaste.server.domain.model.pickup.PickupStatus;

/**
 * Terminal state indicating the pickup request was aborted prior to collection.
 */
public class CancelledState implements PickupState {

    @Override
    public PickupStatus getStatus() {
        return PickupStatus.CANCELLED;
    }
}