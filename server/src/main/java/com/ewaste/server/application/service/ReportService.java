package com.ewaste.server.application.service;

import com.ewaste.server.domain.model.pickup.PickupRequest;
import com.ewaste.server.domain.model.processing.ProcessingRecord;
import com.ewaste.server.domain.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    private final PickupRepository pickupRequestRepository;
    private final EWasteItemRepository eWasteItemRepository;
    private final ProcessingRecordRepository processingRecordRepository;
    private final RewardRepository rewardRepository;
    private final CollectorRepository collectorRepository;

    public ReportService(PickupRepository pickupRequestRepository,
                         EWasteItemRepository eWasteItemRepository,
                         ProcessingRecordRepository processingRecordRepository,
                         RewardRepository rewardRepository,
                         CollectorRepository collectorRepository) {
        this.pickupRequestRepository = pickupRequestRepository;
        this.eWasteItemRepository = eWasteItemRepository;
        this.processingRecordRepository = processingRecordRepository;
        this.rewardRepository = rewardRepository;
        this.collectorRepository = collectorRepository;
    }

    /**
     * Get overall system summary metrics
     */
    public ReportMetrics getSystemSummary() {
        ReportMetrics metrics = new ReportMetrics();

        // Total pickups
        metrics.totalPickups = pickupRequestRepository.findAll().size();

        List<PickupRequest> processedPickups = pickupRequestRepository.findAll().stream()
                .filter(p -> p.isProcessing() || p.isCompleted())
                .toList();

        // Total items processed
        metrics.totalItemsProcessed = processedPickups.stream()
                .mapToInt(p -> p.getItems() != null ? p.getItems().size() : 0)
                .sum();

        // Total recycling points earned
        metrics.totalPointsEarned = rewardRepository.findAll().stream()
                .mapToInt(reward -> reward.getPointsEarned())
                .sum();

        // Total weight recycled includes facility-processing and completed pickups,
        // but excludes submitted, assigned, collected, and delivered-only pickups.
        metrics.totalWeightRecycled = processedPickups.stream()
                .mapToDouble(p -> eWasteItemRepository.calculateTotalWeightByPickupId(p.getPickupId()))
                .sum();

        // Hazardous waste ratio
        long hazardousCount = eWasteItemRepository.findByHazardousStatus(true).size();
        long totalItems = eWasteItemRepository.findAll().size();
        metrics.hazardousWasteRatio = totalItems > 0 ? (double) hazardousCount / totalItems : 0.0;

        // Active collectors
        metrics.activeCollectors = collectorRepository.findAvailable().size();

        // Total collectors
        metrics.totalCollectors = Math.toIntExact(collectorRepository.count());

        return metrics;
    }

    /**
     * Get pickup statistics by date range
     */
    public PickupStatistics getPickupStatistics(String startDate, String endDate) {
        PickupStatistics stats = new PickupStatistics();

        // Parse dates
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate start = LocalDate.parse(startDate, formatter);
        LocalDate end = LocalDate.parse(endDate, formatter);

        // Get pickups in date range
        List<PickupRequest> pickups = pickupRequestRepository.findAll().stream()
                .filter(p -> p.getPreferredDate() != null
                        && !p.getPreferredDate().isBlank()
                        && !LocalDate.parse(p.getPreferredDate()).isBefore(start)
                        && !LocalDate.parse(p.getPreferredDate()).isAfter(end))
                .toList();

        stats.totalPickups = pickups.size();

        // Count by status
        for (PickupRequest pickup : pickups) {
            String status = pickup.getCurrentState();
            stats.pickupsByStatus.put(status, stats.pickupsByStatus.getOrDefault(status, 0) + 1);
        }

        // Calculate average priority score
        stats.averagePriorityScore = pickups.stream()
                .mapToDouble(PickupRequest::getPriorityScore)
                .average()
                .orElse(0.0);

        return stats;
    }

    /**
     * Get collector performance report
     */
    public CollectorPerformanceReport getCollectorPerformance(Long collectorId) {
        CollectorPerformanceReport report = new CollectorPerformanceReport();

        // Get collector details
        report.collectorId = collectorId;
        report.collectorName = collectorRepository.findById(collectorId)
                .map(c -> c.getUser().getFullName())
                .orElse("Unknown");

        // Get completed pickups
        List<PickupRequest> completedPickups = pickupRequestRepository.findByCollectorId(collectorId).stream()
                .filter(PickupRequest::isCompleted)
                .toList();
        report.totalPickupsCompleted = completedPickups.size();

        // Calculate total weight collected
        report.totalWeightCollected = completedPickups.stream()
                .mapToDouble(p -> eWasteItemRepository.calculateTotalWeightByPickupId(p.getPickupId()))
                .sum();

        // Get current workload
        report.currentWorkload = collectorRepository.findById(collectorId)
                .map(c -> c.getCurrentWorkloadKg())
                .orElse(0.0);

        // Get max capacity
        report.maxCapacity = collectorRepository.findById(collectorId)
                .map(c -> c.getMaxCapacityKg())
                .orElse(0.0);

        // Calculate utilization
        report.utilizationPercentage = report.maxCapacity > 0
                ? (report.currentWorkload / report.maxCapacity) * 100
                : 0.0;

        return report;
    }

    /**
     * Get processing records summary
     */
    public ProcessingSummary getProcessingSummary() {
        ProcessingSummary summary = new ProcessingSummary();

        List<ProcessingRecord> records = processingRecordRepository.findAll();

        summary.totalRecordsProcessed = records.size();

        // Count by workflow type
        for (ProcessingRecord record : records) {
            String type = record.getWorkflowType();
            summary.recordsByWorkflowType.put(type,
                    summary.recordsByWorkflowType.getOrDefault(type, 0) + 1);
        }

        // Total points awarded
        summary.totalPointsAwarded = records.stream()
                .mapToInt(ProcessingRecord::getPointsAwarded)
                .sum();

        return summary;
    }

    /**
     * Get reward summary for a customer
     */
    public RewardSummary getCustomerRewardSummary(Long customerId) {
        RewardSummary summary = new RewardSummary();

        // Get all rewards for customer
        List<com.ewaste.server.domain.model.reward.Reward> rewards = rewardRepository.findByCustomerId(customerId);

        summary.totalTransactions = rewards.size();
        summary.totalPointsEarned = rewards.stream()
                .mapToInt(com.ewaste.server.domain.model.reward.Reward::getPointsEarned)
                .sum();
        summary.currentBalance = rewardRepository.getCurrentBalanceByCustomerId(customerId);

        return summary;
    }

    // Inner classes for report data
    public static class ReportMetrics {
        public long totalPickups;
        public long totalItemsProcessed;
        public int totalPointsEarned;
        public double totalWeightRecycled;
        public double hazardousWasteRatio;
        public int activeCollectors;
        public int totalCollectors;
    }

    public static class PickupStatistics {
        public int totalPickups;
        public Map<String, Integer> pickupsByStatus = new HashMap<>();
        public double averagePriorityScore;
    }

    public static class CollectorPerformanceReport {
        public Long collectorId;
        public String collectorName;
        public int totalPickupsCompleted;
        public double totalWeightCollected;
        public double currentWorkload;
        public double maxCapacity;
        public double utilizationPercentage;
    }

    public static class ProcessingSummary {
        public int totalRecordsProcessed;
        public Map<String, Integer> recordsByWorkflowType = new HashMap<>();
        public int totalPointsAwarded;
    }

    public static class RewardSummary {
        public int totalTransactions;
        public int totalPointsEarned;
        public int currentBalance;
    }
}