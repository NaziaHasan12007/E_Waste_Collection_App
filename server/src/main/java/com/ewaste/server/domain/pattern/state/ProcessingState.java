package com.ewaste.server.domain.pattern.state;

import com.ewaste.server.domain.model.pickup.PickupRequest;
import com.ewaste.server.domain.model.pickup.PickupStatus;

/**
 * State indicating items are undergoing technical inspection, sorting, or material extraction.
 */
public class ProcessingState implements PickupState {

    @Override
    public PickupStatus getStatus() {
        return PickupStatus.PROCESSING;
    }

    @Override
    public void complete(PickupRequest context) {
        context.setState(new CompletedState());
    }
}