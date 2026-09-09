package com.ewaste.server.domain.pattern.strategy.assignment;

import com.ewaste.server.domain.model.collector.Collector;
import com.ewaste.server.domain.model.pickup.PickupRequest;

import java.util.List;
import java.util.Optional;

/**
 * Strategy interface defining dynamic collector dispatch selection policies.
 */
@FunctionalInterface
public interface AssignmentStrategy {

    /**
     * Selects the best candidate collector from the eligible candidate pool.
     *
     * @param request the pickup request requiring fulfillment
     * @param candidates available collector personnel
     * @return an Optional holding the selected collector, or empty if no candidate qualifies
     */
    Optional<Collector> selectCollector(PickupRequest request, List<Collector> candidates);
}