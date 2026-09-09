package com.ewaste.server.domain.model.processing;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Recycling center entity representing facilities where e-waste is processed.
 */
public class RecyclingCenter {

    private Long centerId;
    private String name;
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private String phoneNumber;
    private String email;
    private Double processingCapacityKg;
    private Double currentLoadKg;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public RecyclingCenter() {
        this.currentLoadKg = 0.0;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public RecyclingCenter(String name, String address, Double processingCapacityKg) {
        this();
        this.name = name;
        this.address = address;
        this.processingCapacityKg = processingCapacityKg;
    }

    // ========== GETTERS AND SETTERS ==========

    public Long getCenterId() {
        return centerId;
    }

    public void setCenterId(Long centerId) {
        this.centerId = centerId;
    }

    public String getName() {
        return name;
    }

    public String getCenterName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCenterName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Double getProcessingCapacityKg() {
        return processingCapacityKg;
    }

    public double getCapacityKg() {
        return processingCapacityKg != null ? processingCapacityKg : 0.0;
    }

    public void setProcessingCapacityKg(Double processingCapacityKg) {
        this.processingCapacityKg = processingCapacityKg;
    }

    public void setCapacityKg(double capacityKg) {
        this.processingCapacityKg = capacityKg;
    }

    public double getCurrentUtilizationKg() {
        return currentLoadKg != null ? currentLoadKg : 0.0;
    }

    public void setCurrentUtilizationKg(double loadKg) {
        setCurrentLoadKg(loadKg);
    }

    public Double getCurrentLoadKg() {
        return currentLoadKg;
    }

    public void setCurrentLoadKg(Double currentLoadKg) {
        this.currentLoadKg = currentLoadKg != null ? currentLoadKg : 0.0;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive != null ? isActive : true;
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

    public double getUtilizationPercentage() {
        if (processingCapacityKg == null || processingCapacityKg == 0) {
            return 0.0;
        }
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

    public void addLoad(double weightKg) {
        if (currentLoadKg == null) {
            currentLoadKg = 0.0;
        }
        this.currentLoadKg += weightKg;
        this.updatedAt = LocalDateTime.now();
    }

    public void removeLoad(double weightKg) {
        if (currentLoadKg == null) {
            currentLoadKg = 0.0;
        }
        this.currentLoadKg = Math.max(0, this.currentLoadKg - weightKg);
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isActive() {
        return Boolean.TRUE.equals(isActive);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RecyclingCenter)) return false;
        RecyclingCenter that = (RecyclingCenter) o;
        return Objects.equals(centerId, that.centerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(centerId);
    }

    @Override
    public String toString() {
        return "RecyclingCenter{" +
                "centerId=" + centerId +
                ", name='" + name + '\'' +
                ", city='" + city + '\'' +
                ", processingCapacityKg=" + processingCapacityKg +
                ", currentLoadKg=" + currentLoadKg +
                ", isActive=" + isActive +
                '}';
    }
}