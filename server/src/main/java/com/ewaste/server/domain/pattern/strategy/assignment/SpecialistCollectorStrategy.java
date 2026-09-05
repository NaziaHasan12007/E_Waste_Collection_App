package com.ewaste.server.domain.pattern.strategy.assignment;

import com.ewaste.server.domain.model.collector.Collector;
import com.ewaste.server.domain.model.collector.VehicleType;
import com.ewaste.server.domain.model.ewaste.EWasteItem;
import com.ewaste.server.domain.model.pickup.PickupItem;
import com.ewaste.server.domain.model.pickup.PickupRequest;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Evaluates certification and safety constraints: requests containing toxic or
 * hazardous e-waste require collectors with specialized vehicle types (TRUCK/VAN).
 */
public class SpecialistCollectorStrategy implements AssignmentStrategy {

    @Override
    public Optional<Collector> selectCollector(PickupRequest request, List<Collector> candidates) {
        if (request == null || candidates == null || candidates.isEmpty()) {
            return Optional.empty();
        }

        boolean requiresSpecialist = request.getItems() != null && request.getItems().stream()
                .map(PickupItem::getItem)
                .anyMatch(item -> item != null && item.isHazardous());

        return candidates.stream()
                .filter(Collector::isAvailable)
                .filter(c -> {
                    if (requiresSpecialist) {
                        return c.getVehicleType() == VehicleType.TRUCK || c.getVehicleType() == VehicleType.VAN;
                    }
                    return true;
                })
                .min(Comparator.comparingDouble(Collector::getCurrentWorkloadKg));
    }
}