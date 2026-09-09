package com.ewaste.server.domain.model.pickup;

/**
 * Enum representing the lifecycle states of a pickup request.
 * Used by the State pattern for pickup lifecycle management.
 */
public enum PickupStatus {
    SUBMITTED("Submitted", "Pickup has been submitted by customer"),
    REQUESTED("Requested", "Pickup has been requested by customer"),
    ASSIGNED("Assigned", "Pickup has been assigned to a collector"),
    COLLECTED("Collected", "Items have been collected from customer"),
    DELIVERED("Delivered", "Items have been delivered to recycling center"),
    PROCESSING("Processing", "Items are being processed at recycling center"),
    COMPLETED("Completed", "Pickup has been fully completed"),
    CANCELLED("Cancelled", "Pickup has been cancelled");

    private final String displayName;
    private final String description;

    PickupStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return this != COMPLETED && this != CANCELLED;
    }

    public boolean isTerminal() {
        return this == COMPLETED || this == CANCELLED;
    }

    public boolean canTransitionTo(PickupStatus nextStatus) {
        return switch (this) {
            case SUBMITTED -> nextStatus == REQUESTED || nextStatus == CANCELLED;
            case REQUESTED -> nextStatus == ASSIGNED || nextStatus == CANCELLED;
            case ASSIGNED -> nextStatus == COLLECTED || nextStatus == CANCELLED;
            case COLLECTED -> nextStatus == DELIVERED || nextStatus == CANCELLED;
            case DELIVERED -> nextStatus == PROCESSING || nextStatus == CANCELLED;
            case PROCESSING -> nextStatus == COMPLETED || nextStatus == CANCELLED;
            case COMPLETED, CANCELLED -> false;
        };
    }

    public static PickupStatus fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Pickup status cannot be null or blank");
        }
        for (PickupStatus status : values()) {
            if (status.name().equalsIgnoreCase(value.trim()) ||
                    status.displayName.equalsIgnoreCase(value.trim())) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown pickup status: " + value);
    }

    @Override
    public String toString() {
        return displayName;
    }
}