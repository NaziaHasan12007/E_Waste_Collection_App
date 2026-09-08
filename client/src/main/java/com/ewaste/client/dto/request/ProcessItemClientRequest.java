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

    @Override
    public String toString() {
        return "ProcessItemClientRequest{" +
                "pickupId=" + pickupId +
                ", workflowType='" + workflowType + '\'' +
                ", centerId=" + centerId +
                ", pointsAwarded=" + pointsAwarded +
                '}';
    }
}