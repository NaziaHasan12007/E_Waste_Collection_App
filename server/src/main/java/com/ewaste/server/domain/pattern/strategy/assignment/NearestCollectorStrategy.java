package com.ewaste.server.domain.pattern.strategy.assignment;

import com.ewaste.server.domain.model.collector.Collector;
import com.ewaste.server.domain.model.pickup.PickupRequest;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Assigns tasks based on geographical proximity between the pickup address
 * and the collector's designated operational area.
 */
public class NearestCollectorStrategy implements AssignmentStrategy {

    @Override
    public Optional<Collector> selectCollector(PickupRequest request, List<Collector> candidates) {
        if (request == null || candidates == null || candidates.isEmpty()) {
            return Optional.empty();
        }

        String targetAddress = request.getAddress() != null ? request.getAddress().trim().toLowerCase() : "";

        return candidates.stream()
                .filter(Collector::isAvailable)
                .min(Comparator.comparingInt(c -> calculateDistanceProxy(targetAddress, c.getArea())));
    }

    private int calculateDistanceProxy(String targetAddress, String collectorArea) {
        if (collectorArea == null || collectorArea.isBlank()) {
            return 999;
        }
        String area = collectorArea.trim().toLowerCase();
        if (targetAddress.contains(area) || area.contains(targetAddress)) {
            return 0; // Immediate local zone match
        }
        return Math.abs(targetAddress.hashCode() % 100) + 1;
    }
}