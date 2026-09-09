package com.ewaste.client.dto.response;

public class RewardClientResponse {

    private Long rewardId;
    private Long customerId;
    private String customerName;
    private Integer pointsEarned;
    private Integer balance;

    public RewardClientResponse() {}

    // Getters and Setters
    public Long getRewardId() {
        return rewardId;
    }

    public void setRewardId(Long rewardId) {
        this.rewardId = rewardId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Integer getPointsEarned() {
        return pointsEarned;
    }

    public void setPointsEarned(Integer pointsEarned) {
        this.pointsEarned = pointsEarned;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    // ========== HELPER METHODS ==========

    /**
     * Check if customer has enough points for redemption
     */
    public boolean hasEnoughPoints(int requiredPoints) {
        return balance != null && balance >= requiredPoints;
    }

    /**
     * Get tier based on points balance
     */
    public String getTier() {
        if (balance == null) return "BRONZE";
        if (balance >= 2000) return "PLATINUM";
        if (balance >= 1000) return "GOLD";
        if (balance >= 500) return "SILVER";
        return "BRONZE";
    }

    /**
     * Get points needed to reach next tier
     */
    public int getPointsToNextTier() {
        if (balance == null) return 500;
        String tier = getTier();
        return switch (tier) {
            case "BRONZE" -> Math.max(0, 500 - balance);
            case "SILVER" -> Math.max(0, 1000 - balance);
            case "GOLD" -> Math.max(0, 2000 - balance);
            default -> 0;
        };
    }

    /**
     * Get tier display with emoji
     */
    public String getTierDisplay() {
        String tier = getTier();
        return switch (tier) {
            case "PLATINUM" -> "🏆 PLATINUM";
            case "GOLD" -> "🥇 GOLD";
            case "SILVER" -> "🥈 SILVER";
            default -> "🥉 BRONZE";
        };
    }

    /**
     * Calculate points needed for next reward level
     */
    public int getPointsToNextLevel() {
        int nextLevel = getPointsToNextTier();
        return Math.max(0, nextLevel);
    }

    /**
     * Get progress percentage to next tier (0-100)
     */
    public int getProgressToNextTier() {
        if (balance == null) return 0;
        String tier = getTier();
        int currentTierPoints = switch (tier) {
            case "BRONZE" -> 0;
            case "SILVER" -> 500;
            case "GOLD" -> 1000;
            case "PLATINUM" -> 2000;
            default -> 0;
        };
        int nextTierPoints = switch (tier) {
            case "BRONZE" -> 500;
            case "SILVER" -> 1000;
            case "GOLD" -> 2000;
            default -> 2000;
        };

        if (nextTierPoints <= currentTierPoints) return 100;

        int progress = balance - currentTierPoints;
        int total = nextTierPoints - currentTierPoints;
        return Math.min(100, (progress * 100) / total);
    }

    @Override
    public String toString() {
        return "RewardClientResponse{" +
                "rewardId=" + rewardId +
                ", customerId=" + customerId +
                ", customerName='" + customerName + '\'' +
                ", pointsEarned=" + pointsEarned +
                ", balance=" + balance +
                ", tier=" + getTier() +
                '}';
    }
}