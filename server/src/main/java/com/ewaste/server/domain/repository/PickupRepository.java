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

    PickupRequest update(PickupRequest pickup);

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

    default Optional<PickupRequest> findByItemId(Long itemId) {
        return findAll().stream()
                .filter(pickup -> pickup.getItems() != null
                        && pickup.getItems().stream().anyMatch(item -> itemId.equals(item.getItemId())))
                .findFirst();
    }

    /**
     * Find active pickups for a customer (not completed or cancelled)
     */
    default List<PickupRequest> findActiveByCustomerId(Long customerId) {
        return findByCustomerId(customerId).stream().filter(PickupRequest::isActive).toList();
    }

    /**
     * Find active pickups for a collector (not completed or cancelled)
     */
    default List<PickupRequest> findActiveByCollectorId(Long collectorId) {
        return findByCollectorId(collectorId).stream().filter(PickupRequest::isActive).toList();
    }

    /**
     * Find pending pickups (REQUESTED status)
     */
    default List<PickupRequest> findPendingPickups() {
        return findByStatus(PickupStatus.REQUESTED);
    }

    /**
     * Find pickups by date range
     */
    default List<PickupRequest> findByDateRange(String startDate, String endDate) {
        return findAll().stream()
                .filter(p -> p.getPreferredDate() != null
                        && p.getPreferredDate().compareTo(startDate) >= 0
                        && p.getPreferredDate().compareTo(endDate) <= 0)
                .toList();
    }

    /**
     * Update pickup status
     */
    default void updateStatus(Long pickupId, PickupStatus status) {
        findById(pickupId).ifPresent(p -> {
            p.setState(status);
            update(p);
        });
    }

    /**
     * Update pickup status and collector
     */
    default void updateStatusAndCollector(Long pickupId, PickupStatus status, Long collectorId) {
        findById(pickupId).ifPresent(p -> {
            p.setCollectorId(collectorId);
            p.setState(status);
            update(p);
        });
    }

    /**
     * Delete a pickup by ID
     */
    default void deleteById(Long pickupId) {
    }

    /**
     * Count pickups by status
     */
    default long countByStatus(PickupStatus status) {
        return findByStatus(status).size();
    }

    /**
     * Count pickups by customer ID
     */
    default long countByCustomerId(Long customerId) {
        return findByCustomerId(customerId).size();
    }

    /**
     * Get total weight of items in a pickup
     */
    default double getTotalWeight(Long pickupId) {
        return findById(pickupId).map(PickupRequest::getTotalWeight).orElse(0.0);
    }

    default long count() {
        return findAll().size();
    }
}