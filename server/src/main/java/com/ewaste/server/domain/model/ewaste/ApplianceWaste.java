package com.ewaste.server.domain.model.ewaste;

/** Household appliance (fridge, AC unit, washing machine, ...). */
public class ApplianceWaste extends EWasteItem {

    private static final double BASE_POINTS_PER_KG = 4.0;

    public enum ApplianceType {
        REFRIGERATOR,
        AIR_CONDITIONER,
        WASHING_MACHINE,
        MICROWAVE,
        FAN,
        OTHER
    }

    private ApplianceType applianceType;
    private boolean hasRefrigerant;
    private double powerRatingWatts;

    public ApplianceWaste(EWasteCategory category, String modelName, WasteCondition condition, double weightKg,
                          ApplianceType applianceType, boolean hasRefrigerant, double powerRatingWatts) {
        super(category, modelName, condition, weightKg);
        this.applianceType = applianceType == null ? ApplianceType.OTHER : applianceType;
        this.hasRefrigerant = hasRefrigerant;
        this.powerRatingWatts = powerRatingWatts;
    }

    @Override
    public double calculateRewardPoints() {
        double points = getWeightKg() * BASE_POINTS_PER_KG * getCondition().getRewardMultiplier();
        return Math.round(points * 100.0) / 100.0;
    }

    @Override
    public boolean isHazardous() {
        return hasRefrigerant;
    }

    @Override
    public String getProcessingInstructions() {
        if (hasRefrigerant) {
            return "Recover refrigerant gas via certified technician before dismantling.";
        }
        return "Strip metal casing and reusable components for scrap recycling.";
    }

    public ApplianceType getApplianceType() {
        return applianceType;
    }

    public void setApplianceType(ApplianceType applianceType) {
        this.applianceType = applianceType;
    }

    public boolean isHasRefrigerant() {
        return hasRefrigerant;
    }

    public void setHasRefrigerant(boolean hasRefrigerant) {
        this.hasRefrigerant = hasRefrigerant;
    }

    public double getPowerRatingWatts() {
        return powerRatingWatts;
    }

    public void setPowerRatingWatts(double powerRatingWatts) {
        this.powerRatingWatts = powerRatingWatts;
    }
}
