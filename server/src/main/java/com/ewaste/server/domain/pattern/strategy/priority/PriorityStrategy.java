package com.ewaste.server.domain.pattern.strategy.priority;

import com.ewaste.server.domain.model.pickup.PickupRequest;

/**
 * Strategy interface for dynamically calculating pickup dispatch priority scores.
 */
@FunctionalInterface
public interface PriorityStrategy {

    /**
     * Computes a normalized numeric priority score for the given pickup request.
     * Higher values indicate greater operational urgency.
     *
     * @param request the pickup request entity
     * @return the calculated priority score
     */
    double calculatePriority(PickupRequest request);
}