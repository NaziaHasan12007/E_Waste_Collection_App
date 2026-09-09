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

    @Override
    public String toString() {
        return "RewardClientResponse{" +
                "rewardId=" + rewardId +
                ", customerId=" + customerId +
                ", customerName='" + customerName + '\'' +
                ", pointsEarned=" + pointsEarned +
                ", balance=" + balance +
                '}';
    }
}