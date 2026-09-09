package com.ewaste.server.domain.model.ewaste;

import java.util.Objects;

public abstract class EWasteItem {

    /** Suggested downstream handling; used to route items to a processing workflow. */
    public enum ProcessingType {
        RECYCLE,
        REFURBISH,
        REPAIR,
        HAZARDOUS_DISPOSAL
    }

    private Long id;
    private EWasteCategory category;
    private String modelName;
    private WasteCondition condition;
    private double weightKg;

    protected EWasteItem(EWasteCategory category, String modelName, WasteCondition condition, double weightKg) {
        if (category == null) {
            throw new IllegalArgumentException("category is required");
        }
        if (modelName == null || modelName.isBlank()) {
            throw new IllegalArgumentException("modelName is required");
        }
        if (weightKg <= 0) {
            throw new IllegalArgumentException("weightKg must be greater than zero");
        }
        this.category = category;
        this.modelName = modelName;
        this.condition = condition == null ? WasteCondition.WORKING : condition;
        this.weightKg = weightKg;
    }

    /** Reward points earned for this item, given its current condition (maps to rewards.points_earned). */
    public abstract double calculateRewardPoints();

    /** Persisted as {@code ewaste_items.is_hazardous}. */
    public abstract boolean isHazardous();

    /** Short, item-type-specific handling note shown to collectors/inspection staff. */
    public abstract String getProcessingInstructions();

    /** Suggested workflow; the administrator still makes the final call in ProcessingFacade. */
    public ProcessingType getRecommendedProcessing() {
        if (isHazardous()) {
            return ProcessingType.HAZARDOUS_DISPOSAL;
        }
        if (condition == WasteCondition.WORKING || condition == WasteCondition.MINOR_DAMAGE) {
            return ProcessingType.REFURBISH;
        }
        if (condition.isRepairable()) {
            return ProcessingType.REPAIR;
        }
        return ProcessingType.RECYCLE;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EWasteCategory getCategory() {
        return category;
    }

    public void setCategory(EWasteCategory category) {
        this.category = category;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public WasteCondition getCondition() {
        return condition;
    }

    public void setCondition(WasteCondition condition) {
        this.condition = condition;
    }

    public double getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(double weightKg) {
        if (weightKg <= 0) {
            throw new IllegalArgumentException("weightKg must be greater than zero");
        }
        this.weightKg = weightKg;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EWasteItem)) return false;
        EWasteItem that = (EWasteItem) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{id=" + id + ", modelName='" + modelName
                + "', condition=" + condition + ", weightKg=" + weightKg + '}';
    }
}
