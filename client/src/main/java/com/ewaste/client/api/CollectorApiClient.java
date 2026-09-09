package com.ewaste.client.api;

import com.ewaste.client.dto.response.CollectorClientResponse;
import com.ewaste.client.config.ApiConfig;
import java.util.List;
import com.fasterxml.jackson.core.type.TypeReference;

public class CollectorApiClient extends ApiClient {

    private static CollectorApiClient instance;

    private CollectorApiClient() {
        super();
    }

    public static CollectorApiClient getInstance() {
        if (instance == null) {
            instance = new CollectorApiClient();
        }
        return instance;
    }

    public CollectorClientResponse getCollectorProfile(Long collectorId) {
        String endpoint = ApiConfig.getInstance().getCollectorPerformanceEndpoint(collectorId);
        return get(endpoint, CollectorClientResponse.class);
    }

    public List<CollectorClientResponse> getAllCollectors() {
        String endpoint = ApiConfig.getInstance().getCollectorPerformanceEndpoint(0L)
                .replace("/0", ""); // Adjust this based on your actual endpoint
        return get(endpoint, new TypeReference<List<CollectorClientResponse>>() {});
    }

    public CollectorClientResponse updateCollectorAvailability(Long collectorId, boolean available) {
        String endpoint = ApiConfig.getInstance().getCollectorPerformanceEndpoint(collectorId) + "/availability";
        return patch(endpoint, new AvailabilityRequest(available), CollectorClientResponse.class);
    }

    // Inner class for availability update
    private static class AvailabilityRequest {
        private boolean available;
        public AvailabilityRequest(boolean available) { this.available = available; }
        public boolean isAvailable() { return available; }
        public void setAvailable(boolean available) { this.available = available; }
    }
}