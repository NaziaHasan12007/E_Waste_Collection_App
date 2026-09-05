package com.ewaste.server.api.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CollectorSummaryResponse {

    private Long collectorId;
    private Long userId;
    private String name;
    private String phone;
    private String area;
    private String vehicleType;
    private Boolean isHazardousCapable;
    private Boolean isAvailable;
    private Double currentWorkloadKg;
    private Double maxCapacityKg;

    public CollectorSummaryResponse() {}

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public Boolean getIsHazardousCapable() {
        return isHazardousCapable;
    }

    public void setIsHazardousCapable(Boolean isHazardousCapable) {
        this.isHazardousCapable = isHazardousCapable;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public Double getCurrentWorkloadKg() {
        return currentWorkloadKg;
    }

    public void setCurrentWorkloadKg(Double currentWorkloadKg) {
        this.currentWorkloadKg = currentWorkloadKg;
    }

    public Double getMaxCapacityKg() {
        return maxCapacityKg;
    }

    public void setMaxCapacityKg(Double maxCapacityKg) {
        this.maxCapacityKg = maxCapacityKg;
    }
}