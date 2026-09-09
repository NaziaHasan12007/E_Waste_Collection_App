package com.ewaste.server.domain.pattern.strategy.priority;

import com.ewaste.server.domain.model.ewaste.EWasteItem;
import com.ewaste.server.domain.model.pickup.PickupItem;
import com.ewaste.server.domain.model.pickup.PickupRequest;

/**
 * Prioritizes collection requests based on aggregate cargo weight to optimize
 * heavy-vehicle logistics and bulk recycling throughput.
 */
public class WeightPriorityStrategy implements PriorityStrategy {

    private static final double MAX_WEIGHT_BENCHMARK_KG = 100.0;
    private static final double MAX_WEIGHT_SCORE = 50.0;

    @Override
    public double calculatePriority(PickupRequest request) {
        if (request == null || request.getItems() == null || request.getItems().isEmpty()) {
            return 0.0;
        }

        double totalWeightKg = request.getItems().stream()
                .map(PickupItem::getItem)
                .filter(item -> item != null && item.getWeightKg() > 0)
                .mapToDouble(EWasteItem::getWeightKg)
                .sum();

        // Linearly scale weight up to a maximum cap
        double ratio = Math.min(1.0, totalWeightKg / MAX_WEIGHT_BENCHMARK_KG);
        return ratio * MAX_WEIGHT_SCORE;
    }
}