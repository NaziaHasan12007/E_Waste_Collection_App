package com.ewaste.server.domain.repository;

import com.ewaste.server.domain.model.processing.RecyclingCenter;

import java.util.List;
import java.util.Optional;

public interface RecyclingCenterRepository {

    /**
     * Save a recycling center to the database
     */
    RecyclingCenter save(RecyclingCenter center);

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
    List<RecyclingCenter> findActive();

    /**
     * Find recycling centers with available capacity
     */
    List<RecyclingCenter> findAvailable();

    /**
     * Find recycling centers with capacity greater than specified weight
     */
    List<RecyclingCenter> findWithCapacity(double weightKg);

    /**
     * Update center load
     */
    void updateLoad(Long centerId, double loadKg);

    /**
     * Update center active status
     */
    void updateActiveStatus(Long centerId, boolean isActive);

    /**
     * Delete a recycling center by ID
     */
    void deleteById(Long centerId);

    /**
     * Check if a center exists by name
     */
    boolean existsByName(String name);

    /**
     * Get total processing capacity of all centers
     */
    double getTotalProcessingCapacity();

    /**
     * Get total current load of all centers
     */
    double getTotalCurrentLoad();

    /**
     * Count active centers
     */
    long countActive();
}