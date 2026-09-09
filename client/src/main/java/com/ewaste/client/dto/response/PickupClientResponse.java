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

    // Getters and Setters
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

    // Inner class for item summary
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
                '}';
    }
}