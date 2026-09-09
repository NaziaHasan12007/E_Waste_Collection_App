package com.ewaste.server.domain.model.processing;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Processing result entity representing the outcome of processing a pickup item.
 * Contains detailed recycling metrics and environmental impact data.
 */
public class ProcessingResult {

    private Long resultId;
    private Long recordId;
    private ProcessingRecord processingRecord;
    private String outcome;
    private String recyclingMethod;
    private Double carbonCreditsEarned;
    private Double actualWeightKg;
    private String disposalMethod;
    private Boolean isHazardous;
    private String notes;
    private LocalDateTime processedAt;
    private LocalDateTime createdAt;

    public ProcessingResult() {
        this.carbonCreditsEarned = 0.0;
        this.actualWeightKg = 0.0;
        this.isHazardous = false;
        this.processedAt = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
    }

    public ProcessingResult(Long recordId, String outcome, String recyclingMethod) {
        this();
        this.recordId = recordId;
        this.outcome = outcome;
        this.recyclingMethod = recyclingMethod;
    }

    // ========== GETTERS AND SETTERS ==========

    public Long getResultId() {
        return resultId;
    }

    public void setResultId(Long resultId) {
        this.resultId = resultId;
    }

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public ProcessingRecord getProcessingRecord() {
        return processingRecord;
    }

    public void setProcessingRecord(ProcessingRecord processingRecord) {
        this.processingRecord = processingRecord;
        if (processingRecord != null) {
            this.recordId = processingRecord.getRecordId();
        }
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public String getRecyclingMethod() {
        return recyclingMethod;
    }

    public void setRecyclingMethod(String recyclingMethod) {
        this.recyclingMethod = recyclingMethod;
    }

    public Double getCarbonCreditsEarned() {
        return carbonCreditsEarned;
    }

    public void setCarbonCreditsEarned(Double carbonCreditsEarned) {
        this.carbonCreditsEarned = carbonCreditsEarned != null ? carbonCreditsEarned : 0.0;
    }

    public Double getActualWeightKg() {
        return actualWeightKg;
    }

    public void setActualWeightKg(Double actualWeightKg) {
        this.actualWeightKg = actualWeightKg != null ? actualWeightKg : 0.0;
    }

    public String getDisposalMethod() {
        return disposalMethod;
    }

    public void setDisposalMethod(String disposalMethod) {
        this.disposalMethod = disposalMethod;
    }

    public Boolean getIsHazardous() {
        return isHazardous;
    }

    public void setIsHazardous(Boolean isHazardous) {
        this.isHazardous = isHazardous != null ? isHazardous : false;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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

    // ========== HELPER METHODS ==========

    public boolean isSuccessful() {
        return "SUCCESS".equalsIgnoreCase(outcome) ||
                "COMPLETED".equalsIgnoreCase(outcome);
    }

    public boolean isFailed() {
        return "FAILED".equalsIgnoreCase(outcome) ||
                "REJECTED".equalsIgnoreCase(outcome);
    }

    public String getOutcomeDisplay() {
        if (outcome == null) return "Unknown";
        return switch (outcome.toUpperCase()) {
            case "SUCCESS", "COMPLETED" -> "✅ Success";
            case "FAILED", "REJECTED" -> "❌ Failed";
            case "PARTIAL" -> "⚠️ Partial";
            default -> outcome;
        };
    }

    public String getRecyclingMethodDisplay() {
        if (recyclingMethod == null) return "Unknown";
        return switch (recyclingMethod.toUpperCase()) {
            case "MECHANICAL" -> "🔧 Mechanical Recycling";
            case "CHEMICAL" -> "🧪 Chemical Recovery";
            case "PYROMETALLURGY" -> "🔥 Pyrometallurgy";
            case "HYDROMETALLURGY" -> "💧 Hydrometallurgy";
            case "REUSE" -> "🔄 Reuse";
            case "REPAIR" -> "🔧 Repair";
            case "LANDFILL" -> "🗑️ Landfill Disposal";
            default -> recyclingMethod;
        };
    }

    public double getRecyclingEfficiency() {
        if (actualWeightKg == null || actualWeightKg == 0.0) return 0.0;
        // Simplified efficiency calculation
        return actualWeightKg / (actualWeightKg + getWasteLoss()) * 100;
    }

    private double getWasteLoss() {
        // Simplified - in reality this would come from the process
        return 0.1 * (actualWeightKg != null ? actualWeightKg : 0.0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProcessingResult)) return false;
        ProcessingResult that = (ProcessingResult) o;
        return Objects.equals(resultId, that.resultId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(resultId);
    }

    @Override
    public String toString() {
        return "ProcessingResult{" +
                "resultId=" + resultId +
                ", recordId=" + recordId +
                ", outcome='" + outcome + '\'' +
                ", recyclingMethod='" + recyclingMethod + '\'' +
                ", carbonCreditsEarned=" + carbonCreditsEarned +
                ", processedAt=" + processedAt +
                '}';
    }
}