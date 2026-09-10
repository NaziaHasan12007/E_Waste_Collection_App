package com.ewaste.client.dto.response;

import java.time.LocalDateTime;

public class ProcessingOutcomeClientResponse {

    private Long id;
    private Long pickupId;
    private Long itemId;
    private String itemName;
    private String categoryName;
    private String workflowType;  // RECYCLING, REUSE, REPAIR, RECOVERY, DISPOSAL
    private Long centerId;
    private String centerName;
    private Integer pointsAwarded;
    private Double carbonCreditsEarned;
    private String processingStatus;  // PENDING, PROCESSING, COMPLETED, REJECTED
    private String notes;
    private Double actualWeightKg;
    private String recyclingMethod;
    private Boolean isHazardous;
    private String disposalMethod;
    private LocalDateTime processedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ProcessingOutcomeClientResponse() {}

    // ========== GETTERS ==========
    public Long getId() { return id; }
    public Long getPickupId() { return pickupId; }
    public Long getItemId() { return itemId; }
    public String getItemName() { return itemName; }
    public String getCategoryName() { return categoryName; }
    public String getWorkflowType() { return workflowType; }
    public Long getCenterId() { return centerId; }
    public String getCenterName() { return centerName; }
    public Integer getPointsAwarded() { return pointsAwarded; }
    public Double getCarbonCreditsEarned() { return carbonCreditsEarned; }
    public String getProcessingStatus() { return processingStatus; }
    public String getNotes() { return notes; }
    public Double getActualWeightKg() { return actualWeightKg; }
    public String getRecyclingMethod() { return recyclingMethod; }
    public Boolean getIsHazardous() { return isHazardous; }
    public String getDisposalMethod() { return disposalMethod; }
    public LocalDateTime getProcessedAt() { return processedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // ========== SETTERS ==========
    public void setId(Long id) { this.id = id; }
    public void setPickupId(Long pickupId) { this.pickupId = pickupId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public void setWorkflowType(String workflowType) { this.workflowType = workflowType; }
    public void setCenterId(Long centerId) { this.centerId = centerId; }
    public void setCenterName(String centerName) { this.centerName = centerName; }
    public void setPointsAwarded(Integer pointsAwarded) { this.pointsAwarded = pointsAwarded; }
    public void setCarbonCreditsEarned(Double carbonCreditsEarned) {
        this.carbonCreditsEarned = carbonCreditsEarned;
    }
    public void setProcessingStatus(String processingStatus) { this.processingStatus = processingStatus; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setActualWeightKg(Double actualWeightKg) { this.actualWeightKg = actualWeightKg; }
    public void setRecyclingMethod(String recyclingMethod) { this.recyclingMethod = recyclingMethod; }
    public void setIsHazardous(Boolean isHazardous) { this.isHazardous = isHazardous; }
    public void setDisposalMethod(String disposalMethod) { this.disposalMethod = disposalMethod; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // ========== HELPER METHODS ==========
    public boolean isCompleted() {
        return "COMPLETED".equals(processingStatus);
    }

    public boolean isRejected() {
        return "REJECTED".equals(processingStatus);
    }

    public boolean isPending() {
        return "PENDING".equals(processingStatus);
    }

    public String getStatusDisplay() {
        if (processingStatus == null) return "Unknown";
        return processingStatus.substring(0, 1).toUpperCase() +
                processingStatus.substring(1).toLowerCase();
    }

    public String getWorkflowDisplay() {
        if (workflowType == null) return "Unknown";
        return switch (workflowType.toUpperCase()) {
            case "RECYCLE" -> "♻️ Recycling";
            case "REFURBISH" -> "🔄 Refurbish";
            case "HAZARDOUS_DISPOSAL" -> "⚠️ Hazardous Disposal";
            case "RECYCLING" -> "♻️ Recycling";
            case "REUSE" -> "🔄 Reuse";
            case "REPAIR" -> "🔧 Repair";
            case "RECOVERY" -> "⚡ Recovery";
            case "DISPOSAL" -> "🗑️ Disposal";
            default -> workflowType;
        };
    }

    public double getCarbonCreditsDisplay() {
        return carbonCreditsEarned != null ? carbonCreditsEarned : 0.0;
    }

    @Override
    public String toString() {
        return "ProcessingOutcomeClientResponse{" +
                "id=" + id +
                ", pickupId=" + pickupId +
                ", itemName='" + itemName + '\'' +
                ", workflowType='" + workflowType + '\'' +
                ", pointsAwarded=" + pointsAwarded +
                ", processingStatus='" + processingStatus + '\'' +
                '}';
    }
}