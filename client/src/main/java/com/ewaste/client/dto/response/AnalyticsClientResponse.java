package com.ewaste.client.dto.response;

import java.util.Map;

public class AnalyticsClientResponse {

    // System Summary Metrics
    private Long totalPickups;
    private Long totalItemsProcessed;
    private Integer totalPointsEarned;
    private Double totalWeightRecycled;
    private Double hazardousWasteRatio;
    private Integer activeCollectors;
    private Integer totalCollectors;

    // Pickup Statistics
    private Integer pickupTotal;
    private Map<String, Integer> pickupsByStatus;
    private Double averagePriorityScore;

    // Processing Summary
    private Integer totalRecordsProcessed;
    private Map<String, Integer> recordsByWorkflowType;
    private Integer totalPointsAwarded;

    // Reward Summary
    private Integer rewardTotalTransactions;
    private Integer rewardTotalPointsEarned;
    private Integer rewardCurrentBalance;

    public AnalyticsClientResponse() {}

    // Getters and Setters - System Summary
    public Long getTotalPickups() {
        return totalPickups;
    }

    public void setTotalPickups(Long totalPickups) {
        this.totalPickups = totalPickups;
    }

    public Long getTotalItemsProcessed() {
        return totalItemsProcessed;
    }

    public void setTotalItemsProcessed(Long totalItemsProcessed) {
        this.totalItemsProcessed = totalItemsProcessed;
    }

    public Integer getTotalPointsEarned() {
        return totalPointsEarned;
    }

    public void setTotalPointsEarned(Integer totalPointsEarned) {
        this.totalPointsEarned = totalPointsEarned;
    }

    public Double getTotalWeightRecycled() {
        return totalWeightRecycled;
    }

    public void setTotalWeightRecycled(Double totalWeightRecycled) {
        this.totalWeightRecycled = totalWeightRecycled;
    }

    public Double getHazardousWasteRatio() {
        return hazardousWasteRatio;
    }

    public void setHazardousWasteRatio(Double hazardousWasteRatio) {
        this.hazardousWasteRatio = hazardousWasteRatio;
    }

    public Integer getActiveCollectors() {
        return activeCollectors;
    }

    public void setActiveCollectors(Integer activeCollectors) {
        this.activeCollectors = activeCollectors;
    }

    public Integer getTotalCollectors() {
        return totalCollectors;
    }

    public void setTotalCollectors(Integer totalCollectors) {
        this.totalCollectors = totalCollectors;
    }

    // Getters and Setters - Pickup Statistics
    public Integer getPickupTotal() {
        return pickupTotal;
    }

    public void setPickupTotal(Integer pickupTotal) {
        this.pickupTotal = pickupTotal;
    }

    public Map<String, Integer> getPickupsByStatus() {
        return pickupsByStatus;
    }

    public void setPickupsByStatus(Map<String, Integer> pickupsByStatus) {
        this.pickupsByStatus = pickupsByStatus;
    }

    public Double getAveragePriorityScore() {
        return averagePriorityScore;
    }

    public void setAveragePriorityScore(Double averagePriorityScore) {
        this.averagePriorityScore = averagePriorityScore;
    }

    // Getters and Setters - Processing Summary
    public Integer getTotalRecordsProcessed() {
        return totalRecordsProcessed;
    }

    public void setTotalRecordsProcessed(Integer totalRecordsProcessed) {
        this.totalRecordsProcessed = totalRecordsProcessed;
    }

    public Map<String, Integer> getRecordsByWorkflowType() {
        return recordsByWorkflowType;
    }

    public void setRecordsByWorkflowType(Map<String, Integer> recordsByWorkflowType) {
        this.recordsByWorkflowType = recordsByWorkflowType;
    }

    public Integer getTotalPointsAwarded() {
        return totalPointsAwarded;
    }

    public void setTotalPointsAwarded(Integer totalPointsAwarded) {
        this.totalPointsAwarded = totalPointsAwarded;
    }

    // Getters and Setters - Reward Summary
    public Integer getRewardTotalTransactions() {
        return rewardTotalTransactions;
    }

    public void setRewardTotalTransactions(Integer rewardTotalTransactions) {
        this.rewardTotalTransactions = rewardTotalTransactions;
    }

    public Integer getRewardTotalPointsEarned() {
        return rewardTotalPointsEarned;
    }

    public void setRewardTotalPointsEarned(Integer rewardTotalPointsEarned) {
        this.rewardTotalPointsEarned = rewardTotalPointsEarned;
    }

    public Integer getRewardCurrentBalance() {
        return rewardCurrentBalance;
    }

    public void setRewardCurrentBalance(Integer rewardCurrentBalance) {
        this.rewardCurrentBalance = rewardCurrentBalance;
    }

    @Override
    public String toString() {
        return "AnalyticsClientResponse{" +
                "totalPickups=" + totalPickups +
                ", totalItemsProcessed=" + totalItemsProcessed +
                ", totalPointsEarned=" + totalPointsEarned +
                ", totalWeightRecycled=" + totalWeightRecycled +
                ", hazardousWasteRatio=" + hazardousWasteRatio +
                ", activeCollectors=" + activeCollectors +
                ", totalCollectors=" + totalCollectors +
                '}';
    }
}