package com.ewaste.client.dto.response;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.time.LocalDateTime;

public class EWasteItemClientResponse {

    @JsonAlias("itemId")
    private Long id;
    @JsonAlias("modelName")
    private String name;
    private Long categoryId;
    private String categoryName;
    private Double weightKg;
    private String description;
    private Integer quantity;
    @JsonAlias("wasteCondition")
    private String condition;  // GOOD, FAIR, POOR
    private Long pickupId;
    private Double estimatedRecyclingValue;
    private Double carbonCredits;
    private Integer rewardPoints;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public EWasteItemClientResponse() {}

    public EWasteItemClientResponse(Long id, String name, Long categoryId, String categoryName,
                                    Double weightKg, String description, Integer quantity,
                                    String condition, Long pickupId, Double estimatedRecyclingValue,
                                    Double carbonCredits, Integer rewardPoints) {
        this.id = id;
        this.name = name;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.weightKg = weightKg;
        this.description = description;
        this.quantity = quantity;
        this.condition = condition;
        this.pickupId = pickupId;
        this.estimatedRecyclingValue = estimatedRecyclingValue;
        this.carbonCredits = carbonCredits;
        this.rewardPoints = rewardPoints;
    }

    // ========== GETTERS ==========
    public Long getId() { return id; }
    public String getName() { return name; }
    public Long getCategoryId() { return categoryId; }
    public String getCategoryName() { return categoryName; }
    public Double getWeightKg() { return weightKg; }
    public String getDescription() { return description; }
    public Integer getQuantity() { return quantity; }
    public String getCondition() { return condition; }
    public Long getPickupId() { return pickupId; }
    public Double getEstimatedRecyclingValue() { return estimatedRecyclingValue; }
    public Double getCarbonCredits() { return carbonCredits; }
    public Integer getRewardPoints() { return rewardPoints; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // ========== SETTERS ==========
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public void setWeightKg(Double weightKg) { this.weightKg = weightKg; }
    public void setDescription(String description) { this.description = description; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public void setCondition(String condition) { this.condition = condition; }
    public void setPickupId(Long pickupId) { this.pickupId = pickupId; }
    public void setEstimatedRecyclingValue(Double estimatedRecyclingValue) {
        this.estimatedRecyclingValue = estimatedRecyclingValue;
    }
    public void setCarbonCredits(Double carbonCredits) { this.carbonCredits = carbonCredits; }
    public void setRewardPoints(Integer rewardPoints) { this.rewardPoints = rewardPoints; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // ========== HELPER METHODS ==========
    public double getTotalWeightWithQuantity() {
        return weightKg != null && quantity != null ? weightKg * quantity : 0.0;
    }

    public String getConditionDisplay() {
        if (condition == null) return "Unknown";
        return condition.substring(0, 1).toUpperCase() + condition.substring(1).toLowerCase();
    }

    @Override
    public String toString() {
        return "EWasteItemClientResponse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", weightKg=" + weightKg +
                ", quantity=" + quantity +
                ", rewardPoints=" + rewardPoints +
                '}';
    }
}