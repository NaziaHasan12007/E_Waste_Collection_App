package com.ewaste.server.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class EWasteItemRequestDto {

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @NotBlank(message = "Model name is required")
    private String modelName;

    @NotNull(message = "Weight is required")
    @Positive(message = "Weight must be greater than 0")
    private Double weightKg;

    private Boolean isHazardous; // Optional, will use category default if not provided

    @NotBlank(message = "Waste condition is required")
    private String wasteCondition; // WORKING, DAMAGED, NON_FUNCTIONAL, etc.

    private String specificAttributes; // JSON string for additional attributes

    // Constructors
    public EWasteItemRequestDto() {}

    // Getters and Setters
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }

    public Double getWeightKg() { return weightKg; }
    public void setWeightKg(Double weightKg) { this.weightKg = weightKg; }

    public Boolean getIsHazardous() { return isHazardous; }
    public void setIsHazardous(Boolean isHazardous) { this.isHazardous = isHazardous; }

    public String getWasteCondition() { return wasteCondition; }
    public void setWasteCondition(String wasteCondition) { this.wasteCondition = wasteCondition; }

    public String getSpecificAttributes() { return specificAttributes; }
    public void setSpecificAttributes(String specificAttributes) { this.specificAttributes = specificAttributes; }

    @Override
    public String toString() {
        return "EWasteItemRequestDto{" +
                "categoryId=" + categoryId +
                ", modelName='" + modelName + '\'' +
                ", weightKg=" + weightKg +
                ", isHazardous=" + isHazardous +
                ", wasteCondition='" + wasteCondition + '\'' +
                ", specificAttributes='" + specificAttributes + '\'' +
                '}';
    }
}