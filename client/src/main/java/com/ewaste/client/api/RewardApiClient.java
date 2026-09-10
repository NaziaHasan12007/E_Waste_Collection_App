package com.ewaste.client.api;

import com.ewaste.client.config.ApiConfig;
import com.ewaste.client.dto.response.AnalyticsClientResponse;
import com.ewaste.client.dto.response.RewardHistoryClientResponse;
import com.ewaste.client.dto.response.RewardClientResponse;
import java.util.Map;
import java.util.List;
import com.fasterxml.jackson.core.type.TypeReference;

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

    public List<RewardHistoryClientResponse> getRewardHistory(Long userId) {
        String endpoint = ApiConfig.getInstance().getRewardsEndpoint(userId) + "/history";
        return get(endpoint, new TypeReference<List<RewardHistoryClientResponse>>() {});
    }

    public RewardClientResponse redeemPoints(Long userId, int points) {
        String endpoint = ApiConfig.getInstance().getRewardRedemptionEndpoint(userId);
        return post(endpoint, Map.of("points", points), RewardClientResponse.class);
    }
}