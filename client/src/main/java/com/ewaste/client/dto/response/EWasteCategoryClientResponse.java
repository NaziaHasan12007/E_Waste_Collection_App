package com.ewaste.client.dto.response;

import java.time.LocalDateTime;

public class EWasteCategoryClientResponse {

    private Long id;
    private String name;
    private String description;
    private String icon;
    private String color;
    private Double recyclingRate;
    private Double carbonFootprintReduction;
    private Integer rewardPointsPerKg;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public EWasteCategoryClientResponse() {}

    public EWasteCategoryClientResponse(Long id, String name, String description, String icon,
                                        String color, Double recyclingRate,
                                        Double carbonFootprintReduction,
                                        Integer rewardPointsPerKg, Boolean active) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.color = color;
        this.recyclingRate = recyclingRate;
        this.carbonFootprintReduction = carbonFootprintReduction;
        this.rewardPointsPerKg = rewardPointsPerKg;
        this.active = active;
    }

    // ========== GETTERS ==========
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getIcon() { return icon; }
    public String getColor() { return color; }
    public Double getRecyclingRate() { return recyclingRate; }
    public Double getCarbonFootprintReduction() { return carbonFootprintReduction; }
    public Integer getRewardPointsPerKg() { return rewardPointsPerKg; }
    public Boolean getActive() { return active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // ========== SETTERS ==========
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setIcon(String icon) { this.icon = icon; }
    public void setColor(String color) { this.color = color; }
    public void setRecyclingRate(Double recyclingRate) { this.recyclingRate = recyclingRate; }
    public void setCarbonFootprintReduction(Double carbonFootprintReduction) {
        this.carbonFootprintReduction = carbonFootprintReduction;
    }
    public void setRewardPointsPerKg(Integer rewardPointsPerKg) {
        this.rewardPointsPerKg = rewardPointsPerKg;
    }
    public void setActive(Boolean active) { this.active = active; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // ========== HELPER METHODS ==========
    public int calculatePoints(double weightKg) {
        if (rewardPointsPerKg == null || weightKg <= 0) return 0;
        return (int) (rewardPointsPerKg * weightKg);
    }

    public String getStatusDisplay() {
        return active != null && active ? "Active" : "Inactive";
    }

    @Override
    public String toString() {
        return "EWasteCategoryClientResponse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", rewardPointsPerKg=" + rewardPointsPerKg +
                ", active=" + active +
                '}';
    }
}