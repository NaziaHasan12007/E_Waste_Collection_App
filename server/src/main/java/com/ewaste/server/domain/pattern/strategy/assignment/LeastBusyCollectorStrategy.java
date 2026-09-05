package com.ewaste.server.domain.pattern.strategy.assignment;

import com.ewaste.server.domain.model.collector.Collector;
import com.ewaste.server.domain.model.pickup.PickupRequest;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Balances workload by dispatching the request to the available collector
 * carrying the lowest active cargo weight relative to their max capacity.
 */
public class LeastBusyCollectorStrategy implements AssignmentStrategy {

    @Override
    public Optional<Collector> selectCollector(PickupRequest request, List<Collector> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return Optional.empty();
        }

        return candidates.stream()
                .filter(Collector::isAvailable)
                .filter(c -> c.getCurrentWorkloadKg() < c.getMaxCapacityKg())
                .min(Comparator.comparingDouble(Collector::getCurrentWorkloadKg));
    }
}