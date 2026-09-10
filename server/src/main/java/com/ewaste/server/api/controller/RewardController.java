package com.ewaste.server.api.controller;

import com.ewaste.server.api.dto.response.RewardSummaryResponse;
import com.ewaste.server.api.dto.request.RedeemRewardRequest;
import com.ewaste.server.application.service.RewardService;
import com.ewaste.server.domain.model.reward.Reward;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/rewards")
@CrossOrigin(origins = "*")
public class RewardController {

    private final RewardService rewardService;

    public RewardController(RewardService rewardService) {
        this.rewardService = rewardService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<RewardSummaryResponse> getRewardBalance(@PathVariable Long userId) {
        int totalPoints = rewardService.getTotalPointsForUser(userId);
        RewardSummaryResponse response = new RewardSummaryResponse(
                null,
                userId,
                null,
                totalPoints,
                "Current Aggregated Balance",
                null
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{userId}/redeem")
    public ResponseEntity<RewardSummaryResponse> redeemPoints(@PathVariable Long userId,
                                                               @RequestBody RedeemRewardRequest request) {
        Reward reward = rewardService.redeemPoints(userId, request.getPoints());
        return ResponseEntity.ok(new RewardSummaryResponse(
                reward.getRewardId(),
                reward.getUserId(),
                reward.getPickupId(),
                reward.getPoints(),
                reward.getCalculationBasis(),
                reward.getCreatedAt() != null ? reward.getCreatedAt().toString() : null
        ));
    }

    @GetMapping("/{userId}/history")
    public ResponseEntity<List<RewardSummaryResponse>> getRewardHistory(@PathVariable Long userId) {
        List<Reward> rewards = rewardService.getRewardsForUser(userId);
        List<RewardSummaryResponse> response = rewards.stream()
                .map(r -> new RewardSummaryResponse(
                        r.getRewardId(),
                        r.getUserId(),
                        r.getPickupId(),
                        r.getPoints(),
                        r.getCalculationBasis(),
                        r.getCreatedAt() != null ? r.getCreatedAt().toString() : null
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}