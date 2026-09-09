package com.ewaste.client.api;

import com.ewaste.client.dto.response.RewardClientResponse;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;

/**
 * Client for user reward points and point history (/rewards).
 */
public class RewardApiClient extends ApiClient {

    private static final String BASE_PATH = "/rewards";

    public RewardClientResponse getBalance(long customerId) {
        return get(BASE_PATH + "/" + customerId, RewardClientResponse.class);
    }

    public List<RewardClientResponse> getHistory(long customerId) {
        return get(BASE_PATH + "/" + customerId + "/history", new TypeReference<List<RewardClientResponse>>() {});
    }
}