package com.ewaste.client.dto.response;

import java.time.LocalDateTime;

public class RecyclingCenterClientResponse {

    private Long id;
    private String name;
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private String phoneNumber;
    private String email;
    private Double latitude;
    private Double longitude;
    private String centerType;  // COLLECTION, PROCESSING, BOTH
    private String operatingHours;
    private Boolean isActive;
    private Double processingCapacityKg;
    private Double currentLoadKg;
    private Integer totalPickupsProcessed;
    private Double totalWeightProcessed;
    private Double rating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public RecyclingCenterClientResponse() {}

    // ========== GETTERS ==========
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getZipCode() { return zipCode; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail() { return email; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public String getCenterType() { return centerType; }
    public String getOperatingHours() { return operatingHours; }
    public Boolean getIsActive() { return isActive; }
    public Double getProcessingCapacityKg() { return processingCapacityKg; }
    public Double getCurrentLoadKg() { return currentLoadKg; }
    public Integer getTotalPickupsProcessed() { return totalPickupsProcessed; }
    public Double getTotalWeightProcessed() { return totalWeightProcessed; }
    public Double getRating() { return rating; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // ========== SETTERS ==========
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setAddress(String address) { this.address = address; }
    public void setCity(String city) { this.city = city; }
    public void setState(String state) { this.state = state; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setEmail(String email) { this.email = email; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public void setCenterType(String centerType) { this.centerType = centerType; }
    public void setOperatingHours(String operatingHours) { this.operatingHours = operatingHours; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public void setProcessingCapacityKg(Double processingCapacityKg) {
        this.processingCapacityKg = processingCapacityKg;
    }
    public void setCurrentLoadKg(Double currentLoadKg) { this.currentLoadKg = currentLoadKg; }
    public void setTotalPickupsProcessed(Integer totalPickupsProcessed) {
        this.totalPickupsProcessed = totalPickupsProcessed;
    }
    public void setTotalWeightProcessed(Double totalWeightProcessed) {
        this.totalWeightProcessed = totalWeightProcessed;
    }
    public void setRating(Double rating) { this.rating = rating; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // ========== HELPER METHODS ==========
    public double getUtilizationPercentage() {
        if (processingCapacityKg == null || processingCapacityKg == 0) return 0.0;
        return (currentLoadKg != null ? currentLoadKg : 0.0) / processingCapacityKg * 100;
    }

    public boolean isOverloaded() {
        return getUtilizationPercentage() > 90.0;
    }

    public boolean canAcceptProcessing(double weightKg) {
        if (!Boolean.TRUE.equals(isActive)) return false;
        if (processingCapacityKg == null || currentLoadKg == null) return false;
        return (currentLoadKg + weightKg) <= processingCapacityKg;
    }

    public double getRemainingCapacity() {
        if (processingCapacityKg == null || currentLoadKg == null) return 0.0;
        return Math.max(0, processingCapacityKg - currentLoadKg);
    }

    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        if (address != null) sb.append(address);
        if (city != null) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(city);
        }
        if (state != null) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(state);
        }
        if (zipCode != null) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(zipCode);
        }
        return sb.toString();
    }

    public String getCenterTypeDisplay() {
        if (centerType == null) return "Unknown";
        return switch (centerType.toUpperCase()) {
            case "COLLECTION" -> "📦 Collection Center";
            case "PROCESSING" -> "🏭 Processing Center";
            case "BOTH" -> "🏢 Full Service Center";
            default -> centerType;
        };
    }

    public String getAvailabilityDisplay() {
        return Boolean.TRUE.equals(isActive) ? "✅ Active" : "❌ Inactive";
    }

    @Override
    public String toString() {
        return "RecyclingCenterClientResponse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", centerType='" + centerType + '\'' +
                ", utilization=" + String.format("%.1f%%", getUtilizationPercentage()) +
                '}';
    }
}