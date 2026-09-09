package com.ewaste.server.api.dto.response;

public class RewardResponseDto {

    private Long rewardId;
    private Long customerId;
    private String customerName;
    private Integer pointsEarned;
    private Integer balance;

    // Constructors
    public RewardResponseDto() {}

    public RewardResponseDto(Long rewardId, Long customerId, String customerName,
                             Integer pointsEarned, Integer balance) {
        this.rewardId = rewardId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.pointsEarned = pointsEarned;
        this.balance = balance;
    }

    // Getters and Setters
    public Long getRewardId() { return rewardId; }
    public void setRewardId(Long rewardId) { this.rewardId = rewardId; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public Integer getPointsEarned() { return pointsEarned; }
    public void setPointsEarned(Integer pointsEarned) { this.pointsEarned = pointsEarned; }

    public Integer getBalance() { return balance; }
    public void setBalance(Integer balance) { this.balance = balance; }

    @Override
    public String toString() {
        return "RewardResponseDto{" +
                "rewardId=" + rewardId +
                ", customerId=" + customerId +
                ", customerName='" + customerName + '\'' +
                ", pointsEarned=" + pointsEarned +
                ", balance=" + balance +
                '}';
    }
}