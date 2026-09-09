package com.ewaste.server.domain.repository;

import com.ewaste.server.domain.model.processing.RecyclingCenter;

import java.util.List;
import java.util.Optional;

public interface RecyclingCenterRepository {

    /**
     * Save a recycling center to the database
     */
    RecyclingCenter save(RecyclingCenter center);

    RecyclingCenter update(RecyclingCenter center);

    /**
     * Find a recycling center by ID
     */
    Optional<RecyclingCenter> findById(Long centerId);

    /**
     * Find all recycling centers
     */
    List<RecyclingCenter> findAll();

    /**
     * Find active recycling centers
     */
    default List<RecyclingCenter> findActive() {
        return findAll().stream().filter(RecyclingCenter::isActive).toList();
    }

    /**
     * Find recycling centers with available capacity
     */
    default List<RecyclingCenter> findAvailable() {
        return findActive();
    }

    /**
     * Find recycling centers with capacity greater than specified weight
     */
    default List<RecyclingCenter> findWithCapacity(double weightKg) {
        return findAvailable().stream().filter(c -> c.canAcceptProcessing(weightKg)).toList();
    }

    /**
     * Update center load
     */
    default void updateLoad(Long centerId, double loadKg) {
    }

    /**
     * Update center active status
     */
    default void updateActiveStatus(Long centerId, boolean isActive) {
    }

    /**
     * Delete a recycling center by ID
     */
    default void deleteById(Long centerId) {
    }

    /**
     * Check if a center exists by name
     */
    default boolean existsByName(String name) {
        return findAll().stream().anyMatch(c -> name != null && name.equalsIgnoreCase(c.getCenterName()));
    }

    /**
     * Get total processing capacity of all centers
     */
    default double getTotalProcessingCapacity() {
        return findAll().stream().mapToDouble(c -> c.getCapacityKg()).sum();
    }

    /**
     * Get total current load of all centers
     */
    default double getTotalCurrentLoad() {
        return findAll().stream().mapToDouble(RecyclingCenter::getCurrentUtilizationKg).sum();
    }

    /**
     * Count active centers
     */
    default long countActive() {
        return findActive().size();
    }
}