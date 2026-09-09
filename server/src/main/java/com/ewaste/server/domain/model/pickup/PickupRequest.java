package com.ewaste.server.domain.model.pickup;

import com.ewaste.server.domain.model.collector.Collector;
import com.ewaste.server.domain.model.user.User;
import com.ewaste.server.domain.pattern.state.CollectedState;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Core aggregate representing a pickup request.
 * Manages pickup lifecycle with integrated state pattern.
 */
public class PickupRequest {

    private Long pickupId;
    private Long customerId;
    private User customer;
    private Long collectorId;
    private Collector collector;
    private PickupStatus currentState;
    private Double priorityScore;
    private String address;
    private String scheduledDate;
    private String preferredTime;
    private List<PickupItem> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PickupRequest() {
        this.currentState = PickupStatus.REQUESTED;
        this.priorityScore = 0.0;
        this.items = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public PickupRequest(Long customerId, String address, String scheduledDate) {
        this();
        this.customerId = customerId;
        this.address = address;
        this.scheduledDate = scheduledDate;
    }

    // ========== GETTERS AND SETTERS ==========

    public Long getPickupId() {
        return pickupId;
    }

    public void setPickupId(Long pickupId) {
        this.pickupId = pickupId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public User getCustomer() {
        return customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
        if (customer != null) {
            this.customerId = customer.getId();
        }
    }

    public Long getCollectorId() {
        return collectorId;
    }

    public void setCollectorId(Long collectorId) {
        this.collectorId = collectorId;
    }

    public Collector getCollector() {
        return collector;
    }

    public void setCollector(Collector collector) {
        this.collector = collector;
        if (collector != null) {
            this.collectorId = collector.getCollectorId();
        }
    }

    public PickupStatus getCurrentState() {
        return currentState;
    }

    public void setCurrentState(PickupStatus currentState) {
        this.currentState = currentState;
        this.updatedAt = LocalDateTime.now();
    }

    public void setCurrentState(String state) {
        if (state != null) {
            this.currentState = PickupStatus.fromString(state);
            this.updatedAt = LocalDateTime.now();
        }
    }

    public Double getPriorityScore() {
        return priorityScore;
    }

    public void setPriorityScore(Double priorityScore) {
        this.priorityScore = priorityScore != null ? priorityScore : 0.0;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(String scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public String getPreferredTime() {
        return preferredTime;
    }

    public void setPreferredTime(String preferredTime) {
        this.preferredTime = preferredTime;
    }

    public List<PickupItem> getItems() {
        return items;
    }

    public void setItems(List<PickupItem> items) {
        this.items = items != null ? items : new ArrayList<>();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // ========== STATE TRANSITION METHODS ==========

    public void assign(Collector collector) {
        if (currentState != PickupStatus.REQUESTED) {
            throw new IllegalStateException(
                    "Cannot assign pickup in state: " + currentState);
        }
        this.collector = collector;
        this.collectorId = collector != null ? collector.getCollectorId() : null;
        this.currentState = PickupStatus.ASSIGNED;
        this.updatedAt = LocalDateTime.now();
    }

    public void collect() {
        if (currentState != PickupStatus.ASSIGNED) {
            throw new IllegalStateException(
                    "Cannot collect pickup in state: " + currentState);
        }
        this.currentState = PickupStatus.COLLECTED;
        this.updatedAt = LocalDateTime.now();
    }

    public void deliver() {
        if (currentState != PickupStatus.COLLECTED) {
            throw new IllegalStateException(
                    "Cannot deliver pickup in state: " + currentState);
        }
        this.currentState = PickupStatus.DELIVERED;
        this.updatedAt = LocalDateTime.now();
    }

    public void startProcessing() {
        if (currentState != PickupStatus.DELIVERED) {
            throw new IllegalStateException(
                    "Cannot start processing pickup in state: " + currentState);
        }
        this.currentState = PickupStatus.PROCESSING;
        this.updatedAt = LocalDateTime.now();
    }

    public void complete() {
        if (currentState != PickupStatus.PROCESSING) {
            throw new IllegalStateException(
                    "Cannot complete pickup in state: " + currentState);
        }
        this.currentState = PickupStatus.COMPLETED;
        this.updatedAt = LocalDateTime.now();
    }

    public void cancel() {
        if (currentState.isTerminal()) {
            throw new IllegalStateException(
                    "Cannot cancel pickup in terminal state: " + currentState);
        }
        this.currentState = PickupStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean canTransitionTo(PickupStatus nextState) {
        return currentState.canTransitionTo(nextState);
    }

    // ========== HELPER METHODS ==========

    public boolean isActive() {
        return currentState.isActive();
    }

    public boolean isCompleted() {
        return currentState == PickupStatus.COMPLETED;
    }

    public boolean isCancelled() {
        return currentState == PickupStatus.CANCELLED;
    }

    public boolean isAssigned() {
        return currentState == PickupStatus.ASSIGNED;
    }

    public boolean isCollected() {
        return currentState == PickupStatus.COLLECTED;
    }

    public boolean isDelivered() {
        return currentState == PickupStatus.DELIVERED;
    }

    public boolean isProcessing() {
        return currentState == PickupStatus.PROCESSING;
    }

    public double getTotalWeight() {
        if (items == null || items.isEmpty()) {
            return 0.0;
        }
        return items.stream()
                .mapToDouble(PickupItem::getWeightKg)
                .sum();
    }

    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public boolean hasHazardousItems() {
        if (items == null) return false;
        return items.stream().anyMatch(PickupItem::isHazardous);
    }

    public void addItem(PickupItem item) {
        if (items == null) {
            items = new ArrayList<>();
        }
        items.add(item);
    }

    public void addItem(EWasteItem item) {
        if (items == null) {
            items = new ArrayList<>();
        }
        items.add(new PickupItem(this, item));
    }

    public void removeItem(PickupItem item) {
        if (items != null) {
            items.remove(item);
        }
    }

    public String getStatusDisplay() {
        return currentState != null ? currentState.getDisplayName() : "Unknown";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PickupRequest)) return false;
        PickupRequest that = (PickupRequest) o;
        return Objects.equals(pickupId, that.pickupId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pickupId);
    }

    @Override
    public String toString() {
        return "PickupRequest{" +
                "pickupId=" + pickupId +
                ", customerId=" + customerId +
                ", collectorId=" + collectorId +
                ", currentState=" + currentState +
                ", priorityScore=" + priorityScore +
                ", address='" + address + '\'' +
                ", itemCount=" + getItemCount() +
                '}';
    }
    // Add this method for backward compatibility with state pattern
    public void setState(PickupStatus state) {
        this.currentState = state;
        this.updatedAt = LocalDateTime.now();
    }

    // Add this method for backward compatibility with state pattern
    public void setState(String state) {
        if (state != null) {
            this.currentState = PickupStatus.fromString(state);
            this.updatedAt = LocalDateTime.now();
        }
    }

    // Add this method to get state as string
    public String getState() {
        return currentState != null ? currentState.name() : null;
    }

}