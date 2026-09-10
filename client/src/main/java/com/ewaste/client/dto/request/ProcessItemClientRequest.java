package com.ewaste.client.dto.request;

public class ProcessItemClientRequest {

    private Long itemId;
    private Long centerId;
    private String inspectionNotes;
    private String processingResult;

    public ProcessItemClientRequest() {}

    public ProcessItemClientRequest(Long itemId, Long centerId, String inspectionNotes,
                                    String processingResult) {
        this.itemId = itemId;
        this.centerId = centerId;
        this.inspectionNotes = inspectionNotes;
        this.processingResult = processingResult;
    }

    // Getters and Setters
    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getProcessingResult() {
        return processingResult;
    }

    public void setProcessingResult(String processingResult) {
        this.processingResult = processingResult;
    }

    public Long getCenterId() {
        return centerId;
    }

    public void setCenterId(Long centerId) {
        this.centerId = centerId;
    }

    public String getInspectionNotes() {
        return inspectionNotes;
    }

    public void setInspectionNotes(String inspectionNotes) {
        this.inspectionNotes = inspectionNotes;
    }

    // ========== HELPER METHODS ==========

    /**
     * Validate that all required fields are present and valid
     */
    public boolean isValid() {
        return itemId != null && itemId > 0
                && centerId != null && centerId > 0
                && processingResult != null && !processingResult.isBlank();
    }

    /**
     * Get validation error messages
     */
    public String getValidationErrors() {
        StringBuilder errors = new StringBuilder();
        if (itemId == null || itemId <= 0) {
            errors.append("Invalid item ID; ");
        }
        if (centerId == null || centerId <= 0) {
            errors.append("Invalid center ID; ");
        }
        if (processingResult == null || processingResult.isBlank()) {
            errors.append("Processing result is required; ");
        }
        return errors.toString();
    }

    /**
     * Get workflow type display name
     */
    public String getWorkflowDisplay() {
        if (processingResult == null) return "Unknown";
        return switch (processingResult.toUpperCase()) {
            case "RECYCLE" -> "♻️ Recycling";
            case "REUSE" -> "🔄 Reuse";
            case "REPAIR" -> "🔧 Repair";
            case "REFURBISH" -> "🔧 Refurbish";
            case "HAZARDOUS_DISPOSAL" -> "⚠️ Hazardous Disposal";
            default -> processingResult;
        };
    }

    /**
     * Check if workflow is valid
     */
    public boolean isValidWorkflow() {
        if (processingResult == null) return false;
        return switch (processingResult.toUpperCase()) {
            case "RECYCLE", "REUSE", "REPAIR", "REFURBISH", "HAZARDOUS_DISPOSAL" -> true;
            default -> false;
        };
    }

    @Override
    public String toString() {
        return "ProcessItemClientRequest{" +
                "itemId=" + itemId +
                ", processingResult='" + processingResult + '\'' +
                ", centerId=" + centerId +
                ", isValid=" + isValid() +
                '}';
    }
}