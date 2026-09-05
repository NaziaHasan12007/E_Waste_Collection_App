package com.ewaste.server.api.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProcessingOutcomeResponse {

    private Long recordId;
    private Long pickupId;
    private Long itemId;
    private Long centerId;
    private String workflowType;
    private String processingResult;
    private Integer pointsAwarded;
    private String processedAt;

    public ProcessingOutcomeResponse() {}

    public ProcessingOutcomeResponse(Long recordId, Long pickupId, Long itemId, Long centerId,
                                     String workflowType, String processingResult,
                                     Integer pointsAwarded, String processedAt) {
        this.recordId = recordId;
        this.pickupId = pickupId;
        this.itemId = itemId;
        this.centerId = centerId;
        this.workflowType = workflowType;
        this.processingResult = processingResult;
        this.pointsAwarded = pointsAwarded;
        this.processedAt = processedAt;
    }

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

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Long getCenterId() {
        return centerId;
    }

    public void setCenterId(Long centerId) {
        this.centerId = centerId;
    }

    public String getWorkflowType() {
        return workflowType;
    }

    public void setWorkflowType(String workflowType) {
        this.workflowType = workflowType;
    }

    public String getProcessingResult() {
        return processingResult;
    }

    public void setProcessingResult(String processingResult) {
        this.processingResult = processingResult;
    }

    public Integer getPointsAwarded() {
        return pointsAwarded;
    }

    public void setPointsAwarded(Integer pointsAwarded) {
        this.pointsAwarded = pointsAwarded;
    }

    public String getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(String processedAt) {
        this.processedAt = processedAt;
    }
}