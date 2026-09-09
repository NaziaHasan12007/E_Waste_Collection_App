package com.ewaste.server.domain.repository;

import com.ewaste.server.domain.model.collector.Collector;
import com.ewaste.server.domain.model.collector.VehicleType;

import java.util.List;
import java.util.Optional;

public interface CollectorRepository {

    /**
     * Save a collector to the database
     */
    Collector save(Collector collector);

    /**
     * Find a collector by ID
     */
    Optional<Collector> findById(Long collectorId);

    /**
     * Find a collector by user ID
     */
    Optional<Collector> findByUserId(Long userId);

    /**
     * Find all collectors
     */
    List<Collector> findAll();

    /**
     * Find all available collectors
     */
    default List<Collector> findAvailable() {
        return findByAvailability(true);
    }

    List<Collector> findByAvailability(boolean available);

    Collector update(Collector collector);

    /**
     * Find all collectors by vehicle type
     */
    default List<Collector> findByVehicleType(VehicleType vehicleType) {
        return findAll().stream().filter(c -> c.getVehicleType() == vehicleType).toList();
    }

    /**
     * Find collectors with capacity greater than specified weight
     */
    default List<Collector> findAvailableWithCapacity(double weightKg) {
        return findAvailable().stream().filter(c -> c.canAcceptPickup(weightKg)).toList();
    }

    /**
     * Update collector availability
     */
    default void updateAvailability(Long collectorId, boolean isAvailable) {
    }

    /**
     * Update collector workload
     */
    default void updateWorkload(Long collectorId, double workloadKg) {
    }

    /**
     * Delete a collector by ID
     */
    default void deleteById(Long collectorId) {
    }

    /**
     * Check if a collector exists by user ID
     */
    default boolean existsByUserId(Long userId) {
        return findByUserId(userId).isPresent();
    }

    /**
     * Get total number of collectors
     */
    default long count() {
        return findAll().size();
    }

    /**
     * Get count of available collectors
     */
    default long countAvailable() {
        return findAvailable().size();
    }
}