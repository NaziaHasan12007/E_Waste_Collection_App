package com.ewaste.server.domain.model.pickup;

import com.ewaste.server.domain.model.collector.Collector;
import com.ewaste.server.domain.model.ewaste.EWasteItem;
import com.ewaste.server.domain.model.user.User;
import com.ewaste.server.domain.pattern.state.*;

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
    private PickupState state;
    private Double priorityScore;
    private String address;
    private String scheduledDate;
    private String preferredTime;
    private List<PickupItem> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PickupRequest() {
        this.state = new RequestedState();
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
            this.customerId = customer.getUserId();
        }
    }

    public Long getUserId() {
        return customerId;
    }

    public void setUserId(Long userId) {
        this.customerId = userId;
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

    public String getCurrentState() {
        return getStatus() != null ? getStatus().name() : null;
    }

    public void setCurrentState(PickupStatus currentState) {
        setState(currentState);
    }

    public void setCurrentState(String state) {
        if (state != null) {
            setState(PickupStatus.fromString(state));
        }
    }

    public PickupStatus getStatus() {
        return state != null ? state.getStatus() : null;
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

    public String getPreferredDate() {
        return scheduledDate;
    }

    public void setPreferredDate(String preferredDate) {
        this.scheduledDate = preferredDate;
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

    public void request() {
        requireState().request(this);
    }

    public void assign(Collector collector) {
        if (collector == null || collector.getCollectorId() == null) {
            throw new IllegalArgumentException("A collector with a valid ID is required");
        }
        assign(collector.getCollectorId());
        this.collector = collector;
    }

    public void assign(Long collectorId) {
        requireState().assign(this, collectorId);
    }

    public void collect() {
        requireState().collect(this);
    }

    public void deliver() {
        deliver(null);
    }

    public void deliver(Long centerId) {
        requireState().deliver(this, centerId);
    }

    public void startProcessing() {
        process();
    }

    public void process() {
        requireState().process(this);
    }

    public void complete() {
        requireState().complete(this);
    }

    public void cancel() {
        requireState().cancel(this);
    }

    public boolean canTransitionTo(PickupStatus nextState) {
        return getStatus() != null && getStatus().canTransitionTo(nextState);
    }

    // ========== HELPER METHODS ==========

    public boolean isActive() {
        return getStatus() != null && getStatus().isActive();
    }

    public boolean isCompleted() {
        return getStatus() == PickupStatus.COMPLETED;
    }

    public boolean isCancelled() {
        return getStatus() == PickupStatus.CANCELLED;
    }

    public boolean isAssigned() {
        return getStatus() == PickupStatus.ASSIGNED;
    }

    public boolean isCollected() {
        return getStatus() == PickupStatus.COLLECTED;
    }

    public boolean isDelivered() {
        return getStatus() == PickupStatus.DELIVERED;
    }

    public boolean isProcessing() {
        return getStatus() == PickupStatus.PROCESSING;
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
        return getStatus() != null ? getStatus().getDisplayName() : "Unknown";
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
                ", currentState=" + getStatus() +
                ", priorityScore=" + priorityScore +
                ", address='" + address + '\'' +
                ", itemCount=" + getItemCount() +
                '}';
    }
    public void setState(PickupState state) {
        if (state == null) {
            throw new IllegalArgumentException("Pickup state cannot be null");
        }
        this.state = state;
        this.updatedAt = LocalDateTime.now();
    }

    public void setState(PickupStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Pickup status cannot be null");
        }
        setState(toState(status));
    }

    public void setState(String status) {
        if (status != null) {
            setState(PickupStatus.fromString(status));
        }
    }

    public String getState() {
        return getCurrentState();
    }

    private PickupState requireState() {
        if (state == null) {
            throw new IllegalStateException("Pickup state is not initialized");
        }
        return state;
    }

    private PickupState toState(PickupStatus status) {
        return switch (status) {
            case SUBMITTED -> new SubmittedState();
            case REQUESTED -> new RequestedState();
            case ASSIGNED -> new AssignedState();
            case COLLECTED -> new CollectedState();
            case DELIVERED -> new DeliveredState();
            case PROCESSING -> new ProcessingState();
            case COMPLETED -> new CompletedState();
            case CANCELLED -> new CancelledState();
        };
    }

}