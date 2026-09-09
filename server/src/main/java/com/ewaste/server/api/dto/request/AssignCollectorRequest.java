package com.ewaste.server.api.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AssignCollectorRequest {

    private Long collectorId;

    public AssignCollectorRequest() {}

    public AssignCollectorRequest(Long collectorId) {
        this.collectorId = collectorId;
    }

    public Long getCollectorId() {
        return collectorId;
    }

    public void setCollectorId(Long collectorId) {
        this.collectorId = collectorId;
    }
}