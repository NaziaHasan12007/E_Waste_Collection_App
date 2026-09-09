package com.ewaste.client.dto.response;

public class CollectorClientResponse {

    private Long collectorId;
    private Long userId;
    private String fullName;
    private String email;
    private String vehicleType;
    private Double maxCapacityKg;
    private Double currentWorkloadKg;
    private Boolean isAvailable;

    public CollectorClientResponse() {}

    // Getters and Setters
    public Long getCollectorId() {
        return collectorId;
    }

    public void setCollectorId(Long collectorId) {
        this.collectorId = collectorId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public Double getMaxCapacityKg() {
        return maxCapacityKg;
    }

    public void setMaxCapacityKg(Double maxCapacityKg) {
        this.maxCapacityKg = maxCapacityKg;
    }

    public Double getCurrentWorkloadKg() {
        return currentWorkloadKg;
    }

    public void setCurrentWorkloadKg(Double currentWorkloadKg) {
        this.currentWorkloadKg = currentWorkloadKg;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    // ========== HELPER METHODS ==========

    /**
     * Calculate utilization percentage of collector's capacity
     */
    public double getUtilizationPercentage() {
        if (maxCapacityKg == null || maxCapacityKg == 0) {
            return 0.0;
        }
        return (currentWorkloadKg != null ? currentWorkloadKg : 0.0) / maxCapacityKg * 100;
    }

    /**
     * Check if collector can accept a new pickup of given weight
     */
    public boolean canAcceptPickup(double weightKg) {
        if (isAvailable == null || !isAvailable) return false;
        if (maxCapacityKg == null || currentWorkloadKg == null) return false;
        return (currentWorkloadKg + weightKg) <= maxCapacityKg;
    }

    /**
     * Get availability status as readable string
     */
    public String getAvailabilityDisplay() {
        return isAvailable != null && isAvailable ? "Available" : "Unavailable";
    }

    /**
     * Get remaining capacity
     */
    public double getRemainingCapacity() {
        if (maxCapacityKg == null || currentWorkloadKg == null) return 0.0;
        return Math.max(0, maxCapacityKg - currentWorkloadKg);
    }

    /**
     * Check if collector is overloaded (over 90% capacity)
     */
    public boolean isOverloaded() {
        return getUtilizationPercentage() > 90.0;
    }

    @Override
    public String toString() {
        return "CollectorClientResponse{" +
                "collectorId=" + collectorId +
                ", fullName='" + fullName + '\'' +
                ", vehicleType='" + vehicleType + '\'' +
                ", maxCapacityKg=" + maxCapacityKg +
                ", currentWorkloadKg=" + currentWorkloadKg +
                ", isAvailable=" + isAvailable +
                ", utilization=" + String.format("%.1f%%", getUtilizationPercentage()) +
                '}';
    }
}