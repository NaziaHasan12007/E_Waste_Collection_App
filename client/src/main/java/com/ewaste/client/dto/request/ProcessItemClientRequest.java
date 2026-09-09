package com.ewaste.client.dto.request;

public class ProcessItemClientRequest {

    private Long pickupId;
    private String workflowType;
    private Long centerId;
    private Integer pointsAwarded;

    public ProcessItemClientRequest() {}

    public ProcessItemClientRequest(Long pickupId, String workflowType, Long centerId, Integer pointsAwarded) {
        this.pickupId = pickupId;
        this.workflowType = workflowType;
        this.centerId = centerId;
        this.pointsAwarded = pointsAwarded;
    }

    // Getters and Setters
    public Long getPickupId() {
        return pickupId;
    }

    public void setPickupId(Long pickupId) {
        this.pickupId = pickupId;
    }

    public String getWorkflowType() {
        return workflowType;
    }

    public void setWorkflowType(String workflowType) {
        this.workflowType = workflowType;
    }

    public Long getCenterId() {
        return centerId;
    }

    public void setCenterId(Long centerId) {
        this.centerId = centerId;
    }

    public Integer getPointsAwarded() {
        return pointsAwarded;
    }

    public void setPointsAwarded(Integer pointsAwarded) {
        this.pointsAwarded = pointsAwarded;
    }

    // ========== HELPER METHODS ==========

    /**
     * Validate that all required fields are present and valid
     */
    public boolean isValid() {
        return pickupId != null && pickupId > 0
                && workflowType != null && !workflowType.isBlank()
                && centerId != null && centerId > 0
                && pointsAwarded != null && pointsAwarded >= 0;
    }

    /**
     * Get validation error messages
     */
    public String getValidationErrors() {
        StringBuilder errors = new StringBuilder();
        if (pickupId == null || pickupId <= 0) {
            errors.append("Invalid pickup ID; ");
        }
        if (workflowType == null || workflowType.isBlank()) {
            errors.append("Workflow type is required; ");
        }
        if (centerId == null || centerId <= 0) {
            errors.append("Invalid center ID; ");
        }
        if (pointsAwarded == null || pointsAwarded < 0) {
            errors.append("Points awarded must be >= 0; ");
        }
        return errors.toString();
    }

    /**
     * Get workflow type display name
     */
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

    /**
     * Check if workflow is valid
     */
    public boolean isValidWorkflow() {
        if (workflowType == null) return false;
        return switch (workflowType.toUpperCase()) {
            case "RECYCLING", "REUSE", "REPAIR", "RECOVERY", "DISPOSAL" -> true;
            default -> false;
        };
    }

    @Override
    public String toString() {
        return "ProcessItemClientRequest{" +
                "pickupId=" + pickupId +
                ", workflowType='" + workflowType + '\'' +
                ", centerId=" + centerId +
                ", pointsAwarded=" + pointsAwarded +
                ", isValid=" + isValid() +
                '}';
    }
}