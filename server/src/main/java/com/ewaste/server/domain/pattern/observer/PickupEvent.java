package com.ewaste.server.domain.pattern.observer;

import com.ewaste.server.domain.model.pickup.PickupRequest;
import com.ewaste.server.domain.model.pickup.PickupStatus;

import java.time.LocalDateTime;

/**
 * Immutable event object capturing pickup lifecycle state transitions and dispatch metadata.
 */
public class PickupEvent {

    private final Long pickupId;
    private final Long customerId;
    private final Long collectorId;
    private final PickupStatus previousStatus;
    private final PickupStatus currentStatus;
    private final String description;
    private final LocalDateTime timestamp;

    public PickupEvent(PickupRequest pickup, PickupStatus previousStatus, String description) {
        this.pickupId = pickup.getPickupId();
        this.customerId = pickup.getUserId();
        this.collectorId = pickup.getCollectorId();
        this.previousStatus = previousStatus;
        this.currentStatus = pickup.getStatus();
        this.description = description;
        this.timestamp = LocalDateTime.now();
    }

    public PickupEvent(Long pickupId, Long customerId, Long collectorId,
                       PickupStatus previousStatus, PickupStatus currentStatus,
                       String description) {
        this.pickupId = pickupId;
        this.customerId = customerId;
        this.collectorId = collectorId;
        this.previousStatus = previousStatus;
        this.currentStatus = currentStatus;
        this.description = description;
        this.timestamp = LocalDateTime.now();
    }

    public Long getPickupId() {
        return pickupId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public Long getCollectorId() {
        return collectorId;
    }

    public PickupStatus getPreviousStatus() {
        return previousStatus;
    }

    public PickupStatus getCurrentStatus() {
        return currentStatus;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "PickupEvent{" +
                "pickupId=" + pickupId +
                ", currentStatus=" + currentStatus +
                ", timestamp=" + timestamp +
                '}';
    }
}