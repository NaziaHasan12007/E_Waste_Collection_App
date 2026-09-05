package com.ewaste.server.api.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InspectionOutcomeRequest {

    private Long itemId;
    private Long centerId;
    private String inspectionNotes;
    private String processingResult; // REUSE, REPAIR, REFURBISH, RECYCLE, HAZARDOUS_DISPOSAL

    public InspectionOutcomeRequest() {}

    public InspectionOutcomeRequest(Long itemId, Long centerId, String inspectionNotes, String processingResult) {
        this.itemId = itemId;
        this.centerId = centerId;
        this.inspectionNotes = inspectionNotes;
        this.processingResult = processingResult;
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

    public String getInspectionNotes() {
        return inspectionNotes;
    }

    public void setInspectionNotes(String inspectionNotes) {
        this.inspectionNotes = inspectionNotes;
    }

    public String getProcessingResult() {
        return processingResult;
    }

    public void setProcessingResult(String processingResult) {
        this.processingResult = processingResult;
    }
}