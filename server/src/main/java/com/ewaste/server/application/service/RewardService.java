package com.ewaste.server.application.service;

import com.ewaste.server.domain.model.reward.Reward;
import com.ewaste.server.domain.repository.RewardRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RewardService {

    private final RewardRepository rewardRepository;

    public RewardService(RewardRepository rewardRepository) {
        this.rewardRepository = rewardRepository;
    }

    public Reward awardPoints(Long userId, Long pickupId, int points, String calculationBasis) {
        Reward reward = new Reward();
        reward.setUserId(userId);
        reward.setPickupId(pickupId);
        reward.setPoints(points);
        reward.setCalculationBasis(calculationBasis);
        reward.setCreatedAt(LocalDateTime.now());
        return rewardRepository.save(reward);
    }

    public int getTotalPointsForUser(Long userId) {
        return rewardRepository.findTotalPointsByUserId(userId);
    }

    public List<Reward> getRewardsForUser(Long userId) {
        return rewardRepository.findByUserId(userId);
    }
}