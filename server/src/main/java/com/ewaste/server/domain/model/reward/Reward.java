package com.ewaste.server.domain.model.reward;

import java.util.Objects;

public class Reward {

    private Long id;
    private Long customerId;
    private int pointsEarned;
    private int balance;

    public Reward() {
    }
    public Long getRewardId() {
        return id;
    }

    public void setRewardId(Long rewardId) {
        this.id = rewardId;
    }
    public Reward(Long customerId, int pointsEarned, int balance) {
        if (customerId == null) {
            throw new IllegalArgumentException("customerId is required");
        }
        if (pointsEarned < 0) {
            throw new IllegalArgumentException("pointsEarned cannot be negative");
        }
        this.customerId = customerId;
        this.pointsEarned = pointsEarned;
        this.balance = balance;
    }

    /** Convenience factory: builds the next ledger entry given the customer's prior balance. */
    public static Reward earn(Long customerId, int pointsEarned, int previousBalance) {
        return new Reward(customerId, pointsEarned, previousBalance + pointsEarned);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public int getPointsEarned() {
        return pointsEarned;
    }

    public void setPointsEarned(int pointsEarned) {
        this.pointsEarned = pointsEarned;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Reward)) return false;
        Reward reward = (Reward) o;
        return Objects.equals(id, reward.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Reward{id=" + id + ", customerId=" + customerId + ", pointsEarned=" + pointsEarned
                + ", balance=" + balance + '}';
    }
}
