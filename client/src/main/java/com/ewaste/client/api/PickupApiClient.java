package com.ewaste.client.api;

import com.ewaste.client.dto.request.CreatePickupClientRequest;
import com.ewaste.client.dto.response.PickupClientResponse;
import com.fasterxml.jackson.core.type.TypeReference;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Client for pickup requests, lifecycle transitions, and assignments (/api/v1/pickups).
 */
public class PickupApiClient extends ApiClient {

    private static final String BASE_PATH = "/api/v1/pickups";

    public PickupClientResponse createPickup(CreatePickupClientRequest request) {
        return post(BASE_PATH, request, PickupClientResponse.class);
    }

    public PickupClientResponse getPickupById(long pickupId) {
        return get(BASE_PATH + "/" + pickupId, PickupClientResponse.class);
    }

    public List<PickupClientResponse> getAllPickups(String stateFilter) {
        String path = BASE_PATH;
        if (stateFilter != null && !stateFilter.isBlank()) {
            path += "?state=" + URLEncoder.encode(stateFilter.trim(), StandardCharsets.UTF_8);
        }
        return get(path, new TypeReference<List<PickupClientResponse>>() {});
    }

    public List<PickupClientResponse> getPickupsForCustomer(long customerId) {
        return get(BASE_PATH + "/customer/" + customerId, new TypeReference<List<PickupClientResponse>>() {});
    }

    public List<PickupClientResponse> getPickupsForCollector(long collectorId) {
        return get(BASE_PATH + "/collector/" + collectorId, new TypeReference<List<PickupClientResponse>>() {});
    }

    public PickupClientResponse assignCollector(long pickupId, long collectorId) {
        Map<String, Long> payload = Collections.singletonMap("collectorId", collectorId);
        return patch(BASE_PATH + "/" + pickupId + "/assign", payload, PickupClientResponse.class);
    }

    public PickupClientResponse updateState(long pickupId, String newState, Long centerId) {
        StringBuilder path = new StringBuilder(BASE_PATH)
                .append("/")
                .append(pickupId)
                .append("/state?newState=")
                .append(URLEncoder.encode(newState, StandardCharsets.UTF_8));

        if (centerId != null) {
            path.append("&centerId=").append(centerId);
        }
        return patch(path.toString(), null, PickupClientResponse.class);
    }

    public PickupClientResponse cancelPickup(long pickupId) {
        return updateState(pickupId, "CANCEL", null);
    }
}