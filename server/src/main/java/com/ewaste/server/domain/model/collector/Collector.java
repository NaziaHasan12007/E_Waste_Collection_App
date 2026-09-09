package com.ewaste.server.domain.model.collector;

import com.ewaste.server.domain.model.user.User;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Collector entity representing collection personnel.
 * Links to a User account and contains vehicle and capacity information.
 */
public class Collector {

    private Long collectorId;
    private Long userId;
    private User user;
    private String area;
    private VehicleType vehicleType;
    private Double maxCapacityKg;
    private Double currentWorkloadKg;
    private Boolean isAvailable;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Collector() {
        this.currentWorkloadKg = 0.0;
        this.isAvailable = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Collector(Long userId, VehicleType vehicleType, Double maxCapacityKg, String area) {
        this();
        this.userId = userId;
        this.vehicleType = vehicleType;
        this.maxCapacityKg = maxCapacityKg;
        this.area = area;
    }

    // ========== GETTERS AND SETTERS ==========

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            this.userId = user.getId();
        }
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        if (vehicleType != null) {
            this.vehicleType = VehicleType.fromString(vehicleType);
        }
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
        this.currentWorkloadKg = currentWorkloadKg != null ? currentWorkloadKg : 0.0;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable != null ? isAvailable : true;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
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
        if (!Boolean.TRUE.equals(isAvailable)) return false;
        if (maxCapacityKg == null || currentWorkloadKg == null) return false;
        return (currentWorkloadKg + weightKg) <= maxCapacityKg;
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

    /**
     * Add workload to collector
     */
    public void addWorkload(double weightKg) {
        if (currentWorkloadKg == null) {
            currentWorkloadKg = 0.0;
        }
        this.currentWorkloadKg += weightKg;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Remove workload from collector (when pickup is completed or cancelled)
     */
    public void removeWorkload(double weightKg) {
        if (currentWorkloadKg == null) {
            currentWorkloadKg = 0.0;
        }
        this.currentWorkloadKg = Math.max(0, this.currentWorkloadKg - weightKg);
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isAvailable() {
        return Boolean.TRUE.equals(isAvailable);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Collector)) return false;
        Collector collector = (Collector) o;
        return Objects.equals(collectorId, collector.collectorId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(collectorId);
    }

    @Override
    public String toString() {
        return "Collector{" +
                "collectorId=" + collectorId +
                ", userId=" + userId +
                ", vehicleType=" + vehicleType +
                ", maxCapacityKg=" + maxCapacityKg +
                ", currentWorkloadKg=" + currentWorkloadKg +
                ", isAvailable=" + isAvailable +
                '}';
    }
}