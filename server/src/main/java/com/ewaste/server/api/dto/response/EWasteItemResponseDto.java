package com.ewaste.server.api.dto.response;
public class EWasteItemResponseDto {
    private Long itemId;
    private Long categoryId;
    private String categoryName;
    private String modelName;
    private Double weightKg;
    private Boolean isHazardous;
    private String wasteCondition;
    private String specificAttributes;

    // Constructors
    public EWasteItemResponseDto() {}

    public EWasteItemResponseDto(Long itemId, Long categoryId, String categoryName,
                                 String modelName, Double weightKg, Boolean isHazardous,
                                 String wasteCondition, String specificAttributes) {
        this.itemId = itemId;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.modelName = modelName;
        this.weightKg = weightKg;
        this.isHazardous = isHazardous;
        this.wasteCondition = wasteCondition;
        this.specificAttributes = specificAttributes;
    }

    // Getters and Setters
    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

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
        return "EWasteItemResponseDto{" +
                "itemId=" + itemId +
                ", categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                ", modelName='" + modelName + '\'' +
                ", weightKg=" + weightKg +
                ", isHazardous=" + isHazardous +
                ", wasteCondition='" + wasteCondition + '\'' +
                ", specificAttributes='" + specificAttributes + '\'' +
                '}';
    }
}