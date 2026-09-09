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
    List<Collector> findAvailable();

    /**
     * Find all collectors by vehicle type
     */
    List<Collector> findByVehicleType(VehicleType vehicleType);

    /**
     * Find collectors with capacity greater than specified weight
     */
    List<Collector> findAvailableWithCapacity(double weightKg);

    /**
     * Update collector availability
     */
    void updateAvailability(Long collectorId, boolean isAvailable);

    /**
     * Update collector workload
     */
    void updateWorkload(Long collectorId, double workloadKg);

    /**
     * Delete a collector by ID
     */
    void deleteById(Long collectorId);

    /**
     * Check if a collector exists by user ID
     */
    boolean existsByUserId(Long userId);

    /**
     * Get total number of collectors
     */
    long count();

    /**
     * Get count of available collectors
     */
    long countAvailable();
}