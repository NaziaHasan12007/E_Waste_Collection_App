package com.ewaste.client.dto.response;

import java.util.List;

public class PickupClientResponse {

    private Long pickupId;
    private Long customerId;
    private String customerName;
    private Long collectorId;
    private String collectorName;
    private String address;
    private String scheduledDate;
    private String preferredTime;
    private String currentState;
    private Double priorityScore;
    private String createdAt;
    private List<Long> itemIds;
    private List<EWasteItemSummary> items;

    public PickupClientResponse() {}

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

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Long getCollectorId() {
        return collectorId;
    }

    public void setCollectorId(Long collectorId) {
        this.collectorId = collectorId;
    }

    public String getCollectorName() {
        return collectorName;
    }

    public void setCollectorName(String collectorName) {
        this.collectorName = collectorName;
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

    public String getCurrentState() {
        return currentState;
    }

    public void setCurrentState(String currentState) {
        this.currentState = currentState;
    }

    public Double getPriorityScore() {
        return priorityScore;
    }

    public void setPriorityScore(Double priorityScore) {
        this.priorityScore = priorityScore;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List<Long> getItemIds() {
        return itemIds;
    }

    public void setItemIds(List<Long> itemIds) {
        this.itemIds = itemIds;
    }

    public List<EWasteItemSummary> getItems() {
        return items;
    }

    public void setItems(List<EWasteItemSummary> items) {
        this.items = items;
    }

    // ========== HELPER METHODS ==========

    /**
     * Check if the pickup is active (not completed or cancelled)
     */
    public boolean isActive() {
        return currentState != null &&
                !"COMPLETED".equalsIgnoreCase(currentState) &&
                !"CANCELLED".equalsIgnoreCase(currentState);
    }

    /**
     * Check if the pickup is completed
     */
    public boolean isCompleted() {
        return "COMPLETED".equalsIgnoreCase(currentState);
    }

    /**
     * Check if the pickup is cancelled
     */
    public boolean isCancelled() {
        return "CANCELLED".equalsIgnoreCase(currentState);
    }

    /**
     * Check if the pickup is pending
     */
    public boolean isPending() {
        return "PENDING".equalsIgnoreCase(currentState) ||
                "SUBMITTED".equalsIgnoreCase(currentState);
    }

    /**
     * Check if the pickup is assigned
     */
    public boolean isAssigned() {
        return "ASSIGNED".equalsIgnoreCase(currentState);
    }

    /**
     * Check if the pickup is in progress
     */
    public boolean isInProgress() {
        return "IN_PROGRESS".equalsIgnoreCase(currentState);
    }

    /**
     * Check if the pickup is collected
     */
    public boolean isCollected() {
        return "COLLECTED".equalsIgnoreCase(currentState);
    }

    /**
     * Check if the pickup is delivered
     */
    public boolean isDelivered() {
        return "DELIVERED".equalsIgnoreCase(currentState);
    }

    /**
     * Get the status display name
     */
    public String getStatusDisplay() {
        if (currentState == null) return "Unknown";
        return switch (currentState.toUpperCase()) {
            case "PENDING" -> "Pending";
            case "SUBMITTED" -> "Submitted";
            case "ASSIGNED" -> "Assigned";
            case "IN_PROGRESS" -> "In Progress";
            case "COLLECTED" -> "Collected";
            case "DELIVERED" -> "Delivered";
            case "COMPLETED" -> "Completed";
            case "CANCELLED" -> "Cancelled";
            default -> currentState;
        };
    }

    /**
     * Get the status badge style class for CSS styling
     */
    public String getStatusStyleClass() {
        if (currentState == null) return "status-badge";
        return switch (currentState.toUpperCase()) {
            case "PENDING", "SUBMITTED" -> "status-badge-pending";
            case "ASSIGNED" -> "status-badge-assigned";
            case "IN_PROGRESS", "COLLECTED" -> "status-badge-in-progress";
            case "DELIVERED", "COMPLETED" -> "status-badge-completed";
            case "CANCELLED" -> "status-badge-cancelled";
            default -> "status-badge";
        };
    }

    /**
     * Get the total weight of all items in this pickup
     */
    public double getTotalWeight() {
        if (items == null || items.isEmpty()) {
            return 0.0;
        }
        return items.stream()
                .mapToDouble(item -> item.getWeightKg() != null ? item.getWeightKg() : 0.0)
                .sum();
    }

    /**
     * Get the number of items in this pickup
     */
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    /**
     * Check if the pickup has any hazardous items
     */
    public boolean hasHazardousItems() {
        if (items == null) return false;
        return items.stream().anyMatch(item -> Boolean.TRUE.equals(item.getIsHazardous()));
    }

    // ========== INNER CLASS ==========

    public static class EWasteItemSummary {
        private Long itemId;
        private String modelName;
        private String categoryName;
        private Double weightKg;
        private Boolean isHazardous;

        public Long getItemId() { return itemId; }
        public void setItemId(Long itemId) { this.itemId = itemId; }

        public String getModelName() { return modelName; }
        public void setModelName(String modelName) { this.modelName = modelName; }

        public String getCategoryName() { return categoryName; }
        public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

        public Double getWeightKg() { return weightKg; }
        public void setWeightKg(Double weightKg) { this.weightKg = weightKg; }

        public Boolean getIsHazardous() { return isHazardous; }
        public void setIsHazardous(Boolean isHazardous) { this.isHazardous = isHazardous; }
    }

    @Override
    public String toString() {
        return "PickupClientResponse{" +
                "pickupId=" + pickupId +
                ", customerId=" + customerId +
                ", customerName='" + customerName + '\'' +
                ", collectorId=" + collectorId +
                ", collectorName='" + collectorName + '\'' +
                ", address='" + address + '\'' +
                ", scheduledDate='" + scheduledDate + '\'' +
                ", currentState='" + currentState + '\'' +
                ", priorityScore=" + priorityScore +
                ", createdAt='" + createdAt + '\'' +
                ", itemIds=" + itemIds +
                ", items=" + (items != null ? items.size() : 0) + " items" +
                '}';
    }
}