package com.ewaste.server.api.dto.response;

public class EWasteCategoryResponse {

    private Long categoryId;
    private String categoryName;
    private Double basePointsPerKg;
    private Boolean isHazardousDefault;

    // Constructors
    public EWasteCategoryResponse() {}

    public EWasteCategoryResponse(Long categoryId, String categoryName,
                                  Double basePointsPerKg, Boolean isHazardousDefault) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.basePointsPerKg = basePointsPerKg;
        this.isHazardousDefault = isHazardousDefault;
    }

    // Getters and Setters
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public Double getBasePointsPerKg() { return basePointsPerKg; }
    public void setBasePointsPerKg(Double basePointsPerKg) { this.basePointsPerKg = basePointsPerKg; }

    public Boolean getIsHazardousDefault() { return isHazardousDefault; }
    public void setIsHazardousDefault(Boolean isHazardousDefault) { this.isHazardousDefault = isHazardousDefault; }

    @Override
    public String toString() {
        return "EWasteCategoryResponse{" +
                "categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                ", basePointsPerKg=" + basePointsPerKg +
                ", isHazardousDefault=" + isHazardousDefault +
                '}';
    }
}