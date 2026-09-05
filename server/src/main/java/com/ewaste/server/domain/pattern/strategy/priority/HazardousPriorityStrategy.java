package com.ewaste.server.domain.pattern.strategy.priority;

import com.ewaste.server.domain.model.ewaste.EWasteItem;
import com.ewaste.server.domain.model.pickup.PickupItem;
import com.ewaste.server.domain.model.pickup.PickupRequest;

/**
 * Strategy prioritizing requests containing hazardous or toxic components
 * (e.g., lithium batteries, display panels with heavy metals) to minimize environmental risk.
 */
public class HazardousPriorityStrategy implements PriorityStrategy {

    private static final double BASE_HAZARDOUS_SCORE = 50.0;
    private static final double NON_HAZARDOUS_SCORE = 5.0;

    @Override
    public double calculatePriority(PickupRequest request) {
        if (request == null || request.getItems() == null || request.getItems().isEmpty()) {
            return 0.0;
        }

        long hazardousCount = request.getItems().stream()
                .map(PickupItem::getItem)
                .filter(item -> item != null && item.isHazardous())
                .count();

        if (hazardousCount > 0) {
            return BASE_HAZARDOUS_SCORE + (hazardousCount * 10.0);
        }

        return NON_HAZARDOUS_SCORE;
    }
}