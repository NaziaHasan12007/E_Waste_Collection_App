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

    public double getUtilizationPercentage() {
        if (maxCapacityKg == null || maxCapacityKg == 0) {
            return 0.0;
        }
        return (currentWorkloadKg != null ? currentWorkloadKg : 0.0) / maxCapacityKg * 100;
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
                '}';
    }
}