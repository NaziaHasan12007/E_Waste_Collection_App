package com.ewaste.client.api;

import com.ewaste.client.config.ApiConfig;
import com.ewaste.client.dto.response.AnalyticsClientResponse;
import com.ewaste.client.dto.response.RewardClientResponse;

public class RewardApiClient extends ApiClient {

    private static RewardApiClient instance;

    private RewardApiClient() {
        super();
    }

    public static RewardApiClient getInstance() {
        if (instance == null) {
            instance = new RewardApiClient();
        }
        return instance;
    }

    public AnalyticsClientResponse getCustomerRewardSummary(Long userId) {
        String endpoint = ApiConfig.getInstance().getRewardsEndpoint(userId);
        return get(endpoint, AnalyticsClientResponse.class);
    }

    public RewardClientResponse getRewardDetails(Long userId) {
        String endpoint = ApiConfig.getInstance().getRewardsEndpoint(userId);
        return get(endpoint, RewardClientResponse.class);
    }
}