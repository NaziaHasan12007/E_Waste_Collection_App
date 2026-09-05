package com.ewaste.server.api.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RewardSummaryResponse {

    private Long rewardId;
    private Long userId;
    private Long pickupId;
    private Integer points;
    private String calculationBasis;
    private String createdAt;

    public RewardSummaryResponse() {}

    public RewardSummaryResponse(Long rewardId, Long userId, Long pickupId,
                                 Integer points, String calculationBasis, String createdAt) {
        this.rewardId = rewardId;
        this.userId = userId;
        this.pickupId = pickupId;
        this.points = points;
        this.calculationBasis = calculationBasis;
        this.createdAt = createdAt;
    }

    public Long getRewardId() {
        return rewardId;
    }

    public void setRewardId(Long rewardId) {
        this.rewardId = rewardId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getPickupId() {
        return pickupId;
    }

    public void setPickupId(Long pickupId) {
        this.pickupId = pickupId;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public String getCalculationBasis() {
        return calculationBasis;
    }

    public void setCalculationBasis(String calculationBasis) {
        this.calculationBasis = calculationBasis;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}