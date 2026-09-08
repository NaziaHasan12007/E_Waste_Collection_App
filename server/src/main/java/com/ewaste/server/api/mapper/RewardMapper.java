package com.ewaste.server.api.mapper;

import com.ewaste.server.api.dto.response.RewardResponseDto;
import com.ewaste.server.domain.model.reward.Reward;
import com.ewaste.server.domain.model.user.User;
import com.ewaste.server.domain.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RewardMapper {

    private final UserRepository userRepository;

    public RewardMapper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Convert Reward entity to RewardResponseDto
     */
    public RewardResponseDto toResponse(Reward reward) {
        if (reward == null) {
            return null;
        }

        RewardResponseDto dto = new RewardResponseDto();
        dto.setRewardId(reward.getRewardId());
        dto.setCustomerId(reward.getCustomerId());
        dto.setPointsEarned(reward.getPointsEarned());
        dto.setBalance(reward.getBalance());

        // Fetch customer name
        userRepository.findById(reward.getCustomerId())
                .ifPresent(user -> dto.setCustomerName(user.getFullName()));

        return dto;
    }

    /**
     * Convert list of Reward entities to list of RewardResponseDto
     */
    public List<RewardResponseDto> toResponseList(List<Reward> rewards) {
        if (rewards == null) {
            return null;
        }

        return rewards.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Create a Reward entity from processing details
     */
    public Reward toEntity(Long customerId, int pointsEarned, int newBalance) {
        return new Reward(customerId, pointsEarned, newBalance);
    }
}