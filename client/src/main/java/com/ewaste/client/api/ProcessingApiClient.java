package com.ewaste.client.api;

import com.ewaste.client.dto.request.ProcessItemClientRequest;
import com.ewaste.client.dto.response.ProcessingOutcomeClientResponse;
import com.ewaste.client.dto.response.RecyclingCenterClientResponse;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Client for facility processing and inspection endpoints (/processing).
 */
public class ProcessingApiClient extends ApiClient {

    private static ProcessingApiClient instance;
    private static final String BASE_PATH = "/processing";

    private ProcessingApiClient() {
        super();
    }

    public static ProcessingApiClient getInstance() {
        if (instance == null) {
            instance = new ProcessingApiClient();
        }
        return instance;
    }

    public ProcessingOutcomeClientResponse processItem(long pickupId, ProcessItemClientRequest request) {
        return post(BASE_PATH + "/" + pickupId + "/process", request, ProcessingOutcomeClientResponse.class);
    }

    public List<ProcessingOutcomeClientResponse> getProcessingRecords(long pickupId) {
        return get(BASE_PATH + "/records/" + pickupId, new TypeReference<List<ProcessingOutcomeClientResponse>>() {});
    }

    public List<RecyclingCenterClientResponse> getRecyclingCenters() {
        return get(BASE_PATH + "/centers", new TypeReference<List<RecyclingCenterClientResponse>>() {});
    }

    // ========== ADDITIONAL HELPER METHODS ==========

    /**
     * Get processing records for a specific item
     */
    public List<ProcessingOutcomeClientResponse> getProcessingRecordsByItem(long itemId) {
        return get(BASE_PATH + "/items/" + itemId + "/records",
                new TypeReference<List<ProcessingOutcomeClientResponse>>() {});
    }

    /**
     * Get completed processing records for a pickup
     */
    public List<ProcessingOutcomeClientResponse> getCompletedProcessingRecords(long pickupId) {
        List<ProcessingOutcomeClientResponse> allRecords = getProcessingRecords(pickupId);
        if (allRecords == null) return Collections.emptyList();
        return allRecords.stream()
                .filter(ProcessingOutcomeClientResponse::isCompleted)
                .collect(Collectors.toList());
    }

    /**
     * Get pending processing records for a pickup
     */
    public List<ProcessingOutcomeClientResponse> getPendingProcessingRecords(long pickupId) {
        List<ProcessingOutcomeClientResponse> allRecords = getProcessingRecords(pickupId);
        if (allRecords == null) return Collections.emptyList();
        return allRecords.stream()
                .filter(ProcessingOutcomeClientResponse::isPending)
                .collect(Collectors.toList());
    }

    /**
     * Get recycling center by ID
     */
    public RecyclingCenterClientResponse getRecyclingCenterById(long centerId) {
        List<RecyclingCenterClientResponse> centers = getRecyclingCenters();
        if (centers == null) return null;
        return centers.stream()
                .filter(c -> c.getId() != null && c.getId().equals(centerId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Get active recycling centers only
     */
    public List<RecyclingCenterClientResponse> getActiveRecyclingCenters() {
        List<RecyclingCenterClientResponse> centers = getRecyclingCenters();
        if (centers == null) return Collections.emptyList();
        return centers.stream()
                .filter(c -> Boolean.TRUE.equals(c.getIsActive()))
                .collect(Collectors.toList());
    }

    /**
     * Get recycling centers with available capacity
     */
    public List<RecyclingCenterClientResponse> getAvailableRecyclingCenters() {
        List<RecyclingCenterClientResponse> centers = getRecyclingCenters();
        if (centers == null) return Collections.emptyList();
        return centers.stream()
                .filter(c -> Boolean.TRUE.equals(c.getIsActive()) && !c.isOverloaded())
                .collect(Collectors.toList());
    }

    /**
     * Get recycling centers by type
     */
    public List<RecyclingCenterClientResponse> getRecyclingCentersByType(String centerType) {
        List<RecyclingCenterClientResponse> centers = getRecyclingCenters();
        if (centers == null) return Collections.emptyList();
        return centers.stream()
                .filter(c -> c.getCenterType() != null &&
                        c.getCenterType().equalsIgnoreCase(centerType))
                .collect(Collectors.toList());
    }

    /**
     * Get processing summary for a pickup
     */
    public ProcessingSummary getProcessingSummary(long pickupId) {
        List<ProcessingOutcomeClientResponse> records = getProcessingRecords(pickupId);
        if (records == null || records.isEmpty()) {
            return new ProcessingSummary(0, 0, 0, 0.0);
        }

        long total = records.size();
        long completed = records.stream().filter(ProcessingOutcomeClientResponse::isCompleted).count();
        long pending = records.stream().filter(ProcessingOutcomeClientResponse::isPending).count();
        double totalPoints = records.stream()
                .mapToDouble(r -> r.getPointsAwarded() != null ? r.getPointsAwarded() : 0)
                .sum();
        double totalCarbonCredits = records.stream()
                .mapToDouble(r -> r.getCarbonCreditsEarned() != null ? r.getCarbonCreditsEarned() : 0)
                .sum();

        return new ProcessingSummary(total, completed, pending, totalPoints, totalCarbonCredits);
    }

    /**
     * Process multiple items in a pickup
     */
    public List<ProcessingOutcomeClientResponse> processMultipleItems(long pickupId,
                                                                      List<ProcessItemClientRequest> requests) {
        // This would require a batch endpoint on the server
        // For now, process each item individually
        if (requests == null) return Collections.emptyList();
        return requests.stream()
                .map(request -> processItem(pickupId, request))
                .collect(Collectors.toList());
    }

    // ========== INNER CLASSES ==========

    /**
     * Processing summary data class
     */
    public static class ProcessingSummary {
        private final long totalRecords;
        private final long completed;
        private final long pending;
        private final double totalPoints;
        private final double totalCarbonCredits;

        public ProcessingSummary(long totalRecords, long completed, long pending,
                                 double totalPoints, double totalCarbonCredits) {
            this.totalRecords = totalRecords;
            this.completed = completed;
            this.pending = pending;
            this.totalPoints = totalPoints;
            this.totalCarbonCredits = totalCarbonCredits;
        }

        public ProcessingSummary(long totalRecords, long completed, long pending, double totalPoints) {
            this(totalRecords, completed, pending, totalPoints, 0.0);
        }

        public long getTotalRecords() { return totalRecords; }
        public long getCompleted() { return completed; }
        public long getPending() { return pending; }
        public long getRejected() { return totalRecords - completed - pending; }
        public double getTotalPoints() { return totalPoints; }
        public double getTotalCarbonCredits() { return totalCarbonCredits; }
        public double getCompletionPercentage() {
            if (totalRecords == 0) return 0.0;
            return (completed * 100.0) / totalRecords;
        }
        public boolean isFullyProcessed() { return totalRecords > 0 && completed == totalRecords; }

        @Override
        public String toString() {
            return "ProcessingSummary{" +
                    "totalRecords=" + totalRecords +
                    ", completed=" + completed +
                    ", pending=" + pending +
                    ", rejected=" + getRejected() +
                    ", totalPoints=" + totalPoints +
                    ", completionPercentage=" + String.format("%.1f%%", getCompletionPercentage()) +
                    '}';
        }
    }
}