package com.ewaste.server.domain.pattern.strategy.priority;

import com.ewaste.server.domain.model.pickup.PickupRequest;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Progressively escalates a pickup request's priority score the longer
 * it sits pending in the queue, preventing starvation.
 */
public class WaitingTimePriorityStrategy implements PriorityStrategy {

    private static final double POINTS_PER_HOUR = 2.0;
    private static final double MAX_WAITING_SCORE = 40.0;

    @Override
    public double calculatePriority(PickupRequest request) {
        if (request == null || request.getCreatedAt() == null) {
            return 0.0;
        }

        LocalDateTime createdAt = request.getCreatedAt();
        Duration waitDuration = Duration.between(createdAt, LocalDateTime.now());
        long hoursWaiting = Math.max(0, waitDuration.toHours());

        double calculatedScore = hoursWaiting * POINTS_PER_HOUR;
        return Math.min(calculatedScore, MAX_WAITING_SCORE);
    }
}