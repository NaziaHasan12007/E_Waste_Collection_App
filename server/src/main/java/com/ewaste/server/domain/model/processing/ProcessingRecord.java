package com.ewaste.server.domain.model.processing;

import com.ewaste.server.domain.model.pickup.PickupRequest;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Processing record entity representing the processing of a pickup at a recycling center.
 */
public class ProcessingRecord {

    private Long recordId;
    private Long pickupId;
    private PickupRequest pickup;
    private Long centerId;
    private RecyclingCenter recyclingCenter;
    private String workflowType;
    private Integer pointsAwarded;
    private String processingStatus;
    private String notes;
    private Double actualWeightKg;
    private Double carbonCreditsEarned;
    private LocalDateTime processedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ProcessingRecord() {
        this.processingStatus = "PENDING";
        this.pointsAwarded = 0;
        this.carbonCreditsEarned = 0.0;
        this.processedAt = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public ProcessingRecord(Long pickupId, Long centerId, String workflowType) {
        this();
        this.pickupId = pickupId;
        this.centerId = centerId;
        this.workflowType = workflowType;
    }

    // ========== GETTERS AND SETTERS ==========

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public Long getPickupId() {
        return pickupId;
    }

    public void setPickupId(Long pickupId) {
        this.pickupId = pickupId;
    }

    public PickupRequest getPickup() {
        return pickup;
    }

    public void setPickup(PickupRequest pickup) {
        this.pickup = pickup;
        if (pickup != null) {
            this.pickupId = pickup.getPickupId();
        }
    }

    public Long getCenterId() {
        return centerId;
    }

    public void setCenterId(Long centerId) {
        this.centerId = centerId;
    }

    public RecyclingCenter getRecyclingCenter() {
        return recyclingCenter;
    }

    public void setRecyclingCenter(RecyclingCenter recyclingCenter) {
        this.recyclingCenter = recyclingCenter;
        if (recyclingCenter != null) {
            this.centerId = recyclingCenter.getCenterId();
        }
    }

    public String getWorkflowType() {
        return workflowType;
    }

    public void setWorkflowType(String workflowType) {
        this.workflowType = workflowType;
    }

    public Integer getPointsAwarded() {
        return pointsAwarded;
    }

    public void setPointsAwarded(Integer pointsAwarded) {
        this.pointsAwarded = pointsAwarded != null ? pointsAwarded : 0;
    }

    public String getProcessingStatus() {
        return processingStatus;
    }

    public void setProcessingStatus(String processingStatus) {
        this.processingStatus = processingStatus;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Double getActualWeightKg() {
        return actualWeightKg;
    }

    public void setActualWeightKg(Double actualWeightKg) {
        this.actualWeightKg = actualWeightKg;
    }

    public Double getCarbonCreditsEarned() {
        return carbonCreditsEarned;
    }

    public void setCarbonCreditsEarned(Double carbonCreditsEarned) {
        this.carbonCreditsEarned = carbonCreditsEarned != null ? carbonCreditsEarned : 0.0;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
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

    // ========== HELPER METHODS ==========

    public boolean isCompleted() {
        return "COMPLETED".equalsIgnoreCase(processingStatus);
    }

    public boolean isPending() {
        return "PENDING".equalsIgnoreCase(processingStatus);
    }

    public boolean isRejected() {
        return "REJECTED".equalsIgnoreCase(processingStatus);
    }

    public String getWorkflowDisplay() {
        if (workflowType == null) return "Unknown";
        return switch (workflowType.toUpperCase()) {
            case "RECYCLING" -> "♻️ Recycling";
            case "REUSE" -> "🔄 Reuse";
            case "REPAIR" -> "🔧 Repair";
            case "RECOVERY" -> "⚡ Recovery";
            case "DISPOSAL" -> "🗑️ Disposal";
            default -> workflowType;
        };
    }

    public void complete() {
        this.processingStatus = "COMPLETED";
        this.updatedAt = LocalDateTime.now();
    }

    public void reject(String reason) {
        this.processingStatus = "REJECTED";
        this.notes = reason;
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProcessingRecord)) return false;
        ProcessingRecord that = (ProcessingRecord) o;
        return Objects.equals(recordId, that.recordId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(recordId);
    }

    @Override
    public String toString() {
        return "ProcessingRecord{" +
                "recordId=" + recordId +
                ", pickupId=" + pickupId +
                ", centerId=" + centerId +
                ", workflowType='" + workflowType + '\'' +
                ", pointsAwarded=" + pointsAwarded +
                ", processingStatus='" + processingStatus + '\'' +
                ", processedAt=" + processedAt +
                '}';
    }
}