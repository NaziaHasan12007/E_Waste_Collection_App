package com.ewaste.server.domain.repository;

import com.ewaste.server.domain.model.processing.ProcessingRecord;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ProcessingRecordRepository {

    /**
     * Save a processing record to the database
     */
    ProcessingRecord save(ProcessingRecord record);

    /**
     * Find a processing record by ID
     */
    Optional<ProcessingRecord> findById(Long recordId);

    /**
     * Find all processing records
     */
    List<ProcessingRecord> findAll();

    /**
     * Find processing records by pickup ID
     */
    default List<ProcessingRecord> findByPickupId(Long pickupId) {
        return findAll().stream()
                .filter(r -> pickupId != null && pickupId.equals(r.getPickupId()))
                .toList();
    }

    /**
     * Find processing records by center ID
     */
    List<ProcessingRecord> findByCenterId(Long centerId);

    /**
     * Find processing records by workflow type
     */
    default List<ProcessingRecord> findByWorkflowType(String workflowType) {
        return findAll().stream()
                .filter(r -> workflowType != null && workflowType.equalsIgnoreCase(r.getWorkflowType()))
                .toList();
    }

    /**
     * Find processing records by status
     */
    default List<ProcessingRecord> findByStatus(String status) {
        return findAll().stream()
                .filter(r -> status != null && status.equalsIgnoreCase(r.getProcessingStatus()))
                .toList();
    }

    /**
     * Find processing records by date range
     */
    default List<ProcessingRecord> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return findAll().stream()
                .filter(r -> r.getProcessedAt() != null
                        && !r.getProcessedAt().isBefore(startDate)
                        && !r.getProcessedAt().isAfter(endDate))
                .toList();
    }

    /**
     * Find completed processing records
     */
    default List<ProcessingRecord> findCompleted() {
        return findByStatus("COMPLETED");
    }

    /**
     * Find pending processing records
     */
    default List<ProcessingRecord> findPending() {
        return findByStatus("PENDING");
    }

    /**
     * Update processing record status
     */
    default void updateStatus(Long recordId, String status) {
    }

    /**
     * Update points awarded
     */
    default void updatePointsAwarded(Long recordId, int pointsAwarded) {
    }

    /**
     * Delete a processing record by ID
     */
    default void deleteById(Long recordId) {
    }

    /**
     * Delete processing records by pickup ID
     */
    default void deleteByPickupId(Long pickupId) {
    }

    /**
     * Count records by status
     */
    default long countByStatus(String status) {
        return findByStatus(status).size();
    }

    /**
     * Get total points awarded for a customer
     */
    default int getTotalPointsByCustomerId(Long customerId) {
        return 0;
    }

    /**
     * Get total carbon credits earned by a customer
     */
    default double getTotalCarbonCreditsByCustomerId(Long customerId) {
        return 0.0;
    }
}