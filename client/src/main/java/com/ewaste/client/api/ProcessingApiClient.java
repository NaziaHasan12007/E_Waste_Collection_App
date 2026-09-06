package com.ewaste.client.api;

import com.ewaste.client.dto.request.ProcessItemClientRequest;
import com.ewaste.client.dto.response.ProcessingOutcomeClientResponse;
import com.ewaste.client.dto.response.RecyclingCenterClientResponse;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;

/**
 * Client for facility processing and inspection endpoints (/processing).
 */
public class ProcessingApiClient extends ApiClient {

    private static final String BASE_PATH = "/processing";

    public ProcessingOutcomeClientResponse processItem(long pickupId, ProcessItemClientRequest request) {
        return post(BASE_PATH + "/" + pickupId + "/process", request, ProcessingOutcomeClientResponse.class);
    }

    public List<ProcessingOutcomeClientResponse> getProcessingRecords(long pickupId) {
        return get(BASE_PATH + "/records/" + pickupId, new TypeReference<List<ProcessingOutcomeClientResponse>>() {});
    }

    public List<RecyclingCenterClientResponse> getRecyclingCenters() {
        return get(BASE_PATH + "/centers", new TypeReference<List<RecyclingCenterClientResponse>>() {});
    }
}