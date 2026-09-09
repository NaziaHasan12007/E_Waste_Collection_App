package com.ewaste.client.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EWasteItemClientRequest {

    @JsonProperty("modelName")
    private String name;
    private Long categoryId;
    private String categoryName;
    private Double weightKg;
    @JsonProperty("specificAttributes")
    private String description;
    private Integer quantity;
    @JsonProperty("wasteCondition")
    private String condition;  // GOOD, FAIR, POOR
    @JsonProperty("isHazardous")
    private Boolean hazardous;
    private Long pickupId;

    public EWasteItemClientRequest() {}

    public EWasteItemClientRequest(String name, Long categoryId, Double weightKg,
                                   String description, Integer quantity, String condition) {
        this.name = name;
        this.categoryId = categoryId;
        this.weightKg = weightKg;
        this.description = description;
        this.quantity = quantity;
        this.condition = condition;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public Double getWeightKg() { return weightKg; }
    public void setWeightKg(Double weightKg) { this.weightKg = weightKg; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }

    public Boolean getHazardous() { return hazardous; }
    public void setHazardous(Boolean hazardous) { this.hazardous = hazardous; }

    public Long getPickupId() { return pickupId; }
    public void setPickupId(Long pickupId) { this.pickupId = pickupId; }

    @Override
    public String toString() {
        return "EWasteItemClientRequest{" +
                "name='" + name + '\'' +
                ", categoryId=" + categoryId +
                ", weightKg=" + weightKg +
                ", quantity=" + quantity +
                '}';
    }
}