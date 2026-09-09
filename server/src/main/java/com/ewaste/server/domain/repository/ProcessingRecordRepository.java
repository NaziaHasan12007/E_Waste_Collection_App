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
    List<ProcessingRecord> findByPickupId(Long pickupId);

    /**
     * Find processing records by center ID
     */
    List<ProcessingRecord> findByCenterId(Long centerId);

    /**
     * Find processing records by workflow type
     */
    List<ProcessingRecord> findByWorkflowType(String workflowType);

    /**
     * Find processing records by status
     */
    List<ProcessingRecord> findByStatus(String status);

    /**
     * Find processing records by date range
     */
    List<ProcessingRecord> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find completed processing records
     */
    List<ProcessingRecord> findCompleted();

    /**
     * Find pending processing records
     */
    List<ProcessingRecord> findPending();

    /**
     * Update processing record status
     */
    void updateStatus(Long recordId, String status);

    /**
     * Update points awarded
     */
    void updatePointsAwarded(Long recordId, int pointsAwarded);

    /**
     * Delete a processing record by ID
     */
    void deleteById(Long recordId);

    /**
     * Delete processing records by pickup ID
     */
    void deleteByPickupId(Long pickupId);

    /**
     * Count records by status
     */
    long countByStatus(String status);

    /**
     * Get total points awarded for a customer
     */
    int getTotalPointsByCustomerId(Long customerId);

    /**
     * Get total carbon credits earned by a customer
     */
    double getTotalCarbonCreditsByCustomerId(Long customerId);
}