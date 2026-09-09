package com.ewaste.server.domain.repository;

import com.ewaste.server.domain.model.reward.Reward;
import java.util.List;
import java.util.Optional;

public interface RewardRepository {
    /**
     * Save a reward record to the database
     * @param reward The reward to save
     * @return The saved reward with generated ID
     */
    Reward save(Reward reward);

    List<Reward> findAll();

    /**
     * Find a reward by its ID
     * @param rewardId The reward ID
     * @return Optional containing the reward if found
     */
    Optional<Reward> findById(Long rewardId);

    /**
     * Find all rewards for a customer
     * @param customerId The customer ID
     * @return List of rewards for the customer
     */
    List<Reward> findByCustomerId(Long customerId);

    default List<Reward> findByUserId(Long userId) {
        return findByCustomerId(userId);
    }

    default int findTotalPointsByUserId(Long userId) {
        return getCurrentBalanceByCustomerId(userId);
    }

    /**
     * Get the current reward balance for a customer
     * @param customerId The customer ID
     * @return The current balance, or 0 if no rewards exist
     */
    int getCurrentBalanceByCustomerId(Long customerId);

    /**
     * Find rewards by minimum points
     * @param minPoints The minimum points threshold
     * @return List of rewards with points >= minPoints
     */
    List<Reward> findByPointsEarnedGreaterThanEqual(int minPoints);

    /**
     * Delete a reward by its ID
     * @param rewardId The reward ID to delete
     */
    void deleteById(Long rewardId);

    /**
     * Delete all rewards for a customer
     * @param customerId The customer ID
     */
    void deleteByCustomerId(Long customerId);
}