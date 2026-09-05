package com.ewaste.server.domain.pattern.state;

import com.ewaste.server.common.exception.BusinessRuleException;
import com.ewaste.server.domain.model.pickup.PickupRequest;
import com.ewaste.server.domain.model.pickup.PickupStatus;

/**
 * State indicating the collector has physically retrieved the waste items.
 */
public class CollectedState implements PickupState {

    @Override
    public PickupStatus getStatus() {
        return PickupStatus.COLLECTED;
    }

    @Override
    public void deliver(PickupRequest context, Long centerId) {
        if (centerId == null || centerId <= 0) {
            throw new BusinessRuleException("A valid recycling center ID must be provided upon delivery.");
        }
        context.setState(new DeliveredState());
    }
}