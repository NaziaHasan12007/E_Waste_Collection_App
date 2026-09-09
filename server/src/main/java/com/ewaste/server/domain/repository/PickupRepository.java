package com.ewaste.server.domain.repository;

import com.ewaste.server.domain.model.pickup.PickupRequest;
import com.ewaste.server.domain.model.pickup.PickupStatus;

import java.util.List;
import java.util.Optional;

public interface PickupRepository {

    /**
     * Save a pickup request to the database
     */
    PickupRequest save(PickupRequest pickup);

    /**
     * Find a pickup by ID
     */
    Optional<PickupRequest> findById(Long pickupId);

    /**
     * Find all pickups
     */
    List<PickupRequest> findAll();

    /**
     * Find pickups by customer ID
     */
    List<PickupRequest> findByCustomerId(Long customerId);

    /**
     * Find pickups by collector ID
     */
    List<PickupRequest> findByCollectorId(Long collectorId);

    /**
     * Find pickups by status
     */
    List<PickupRequest> findByStatus(PickupStatus status);

    /**
     * Find pickups by status as string
     */
    List<PickupRequest> findByStatus(String status);

    /**
     * Find active pickups for a customer (not completed or cancelled)
     */
    List<PickupRequest> findActiveByCustomerId(Long customerId);

    /**
     * Find active pickups for a collector (not completed or cancelled)
     */
    List<PickupRequest> findActiveByCollectorId(Long collectorId);

    /**
     * Find pending pickups (REQUESTED status)
     */
    List<PickupRequest> findPendingPickups();

    /**
     * Find pickups by date range
     */
    List<PickupRequest> findByDateRange(String startDate, String endDate);

    /**
     * Update pickup status
     */
    void updateStatus(Long pickupId, PickupStatus status);

    /**
     * Update pickup status and collector
     */
    void updateStatusAndCollector(Long pickupId, PickupStatus status, Long collectorId);

    /**
     * Delete a pickup by ID
     */
    void deleteById(Long pickupId);

    /**
     * Count pickups by status
     */
    long countByStatus(PickupStatus status);

    /**
     * Count pickups by customer ID
     */
    long countByCustomerId(Long customerId);

    /**
     * Get total weight of items in a pickup
     */
    double getTotalWeight(Long pickupId);
}