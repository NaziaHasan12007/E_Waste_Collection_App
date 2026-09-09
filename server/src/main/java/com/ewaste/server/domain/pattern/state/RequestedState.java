package com.ewaste.server.domain.pattern.state;

import com.ewaste.server.common.exception.BusinessRuleException;
import com.ewaste.server.domain.model.pickup.PickupRequest;
import com.ewaste.server.domain.model.pickup.PickupStatus;

/**
 * State indicating items have been prioritized and are pending collector assignment.
 */
public class RequestedState implements PickupState {

    @Override
    public PickupStatus getStatus() {
        return PickupStatus.REQUESTED;
    }

    @Override
    public void assign(PickupRequest context, Long collectorId) {
        if (collectorId == null || collectorId <= 0) {
            throw new BusinessRuleException("A valid collector ID must be provided to assign a pickup.");
        }
        context.setCollectorId(collectorId);
        context.setState(new AssignedState());
    }

    @Override
    public void cancel(PickupRequest context) {
        context.setState(new CancelledState());
    }
}