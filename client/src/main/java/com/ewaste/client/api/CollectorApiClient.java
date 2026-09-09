package com.ewaste.client.api;

import com.ewaste.client.dto.response.CollectorClientResponse;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Client for collector discovery and availability configuration (/api/v1/collectors).
 */
public class CollectorApiClient extends ApiClient {

    private static final String BASE_PATH = "/api/v1/collectors";

    public List<CollectorClientResponse> getAllCollectors() {
        return get(BASE_PATH, new TypeReference<List<CollectorClientResponse>>() {});
    }

    public List<CollectorClientResponse> getAvailableCollectors() {
        return get(BASE_PATH + "/available", new TypeReference<List<CollectorClientResponse>>() {});
    }

    public CollectorClientResponse getCollectorById(long collectorId) {
        return get(BASE_PATH + "/" + collectorId, CollectorClientResponse.class);
    }

    public CollectorClientResponse updateAvailability(long collectorId, boolean available) {
        Map<String, Boolean> payload = Collections.singletonMap("available", available);
        return patch(BASE_PATH + "/" + collectorId + "/availability", payload, CollectorClientResponse.class);
    }
}