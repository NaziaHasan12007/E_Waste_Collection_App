package com.ewaste.server.domain.model.ewaste;

import java.util.Objects;

/** Maps exactly to the {@code ewaste_categories} table. */
public class EWasteCategory {

    private Long id;
    private String name;
    private double basePointsPerKg;
    private boolean hazardousDefault;

    public EWasteCategory() {
    }

    public EWasteCategory(String name, double basePointsPerKg, boolean hazardousDefault) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Category name is required");
        }
        if (basePointsPerKg < 0) {
            throw new IllegalArgumentException("basePointsPerKg cannot be negative");
        }
        this.name = name;
        this.basePointsPerKg = basePointsPerKg;
        this.hazardousDefault = hazardousDefault;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getCategoryId() {
        return id;
    }

    public void setCategoryId(Long categoryId) {
        this.id = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getCategoryName() {
        return name;
    }

    public void setCategoryName(String categoryName) {
        this.name = categoryName;
    }

    public double getBasePointsPerKg() {
        return basePointsPerKg;
    }

    public void setBasePointsPerKg(double basePointsPerKg) {
        if (basePointsPerKg < 0) {
            throw new IllegalArgumentException("basePointsPerKg cannot be negative");
        }
        this.basePointsPerKg = basePointsPerKg;
    }

    public boolean isHazardousDefault() {
        return hazardousDefault;
    }

    public void setHazardousDefault(boolean hazardousDefault) {
        this.hazardousDefault = hazardousDefault;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EWasteCategory)) return false;
        EWasteCategory that = (EWasteCategory) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "EWasteCategory{id=" + id + ", name='" + name + "', hazardousDefault=" + hazardousDefault + '}';
    }
}
