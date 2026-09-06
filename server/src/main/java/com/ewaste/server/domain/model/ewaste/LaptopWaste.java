package com.ewaste.server.domain.model.ewaste;

/** Laptop / notebook computer. */
public class LaptopWaste extends EWasteItem {

    private static final double BASE_POINTS_PER_KG = 12.0;
    private static final double DATA_WIPE_BONUS = 5.0;

    private boolean hasBattery;
    private boolean hasHardDrive;
    private double screenSizeInches;

    public LaptopWaste(EWasteCategory category, String modelName, WasteCondition condition, double weightKg,
                       boolean hasBattery, boolean hasHardDrive, double screenSizeInches) {
        super(category, modelName, condition, weightKg);
        this.hasBattery = hasBattery;
        this.hasHardDrive = hasHardDrive;
        this.screenSizeInches = screenSizeInches;
    }

    @Override
    public double calculateRewardPoints() {
        double points = getWeightKg() * BASE_POINTS_PER_KG * getCondition().getRewardMultiplier();
        if (hasHardDrive) {
            points += DATA_WIPE_BONUS; // covers the secure data-wipe step
        }
        return Math.round(points * 100.0) / 100.0;
    }

    @Override
    public boolean isHazardous() {
        return hasBattery && (getCondition() == WasteCondition.NON_FUNCTIONAL
                || getCondition() == WasteCondition.SCRAP_ONLY);
    }

    @Override
    public String getProcessingInstructions() {
        StringBuilder sb = new StringBuilder("Remove and securely wipe any storage media.");
        if (hasBattery) {
            sb.append(" Detach the lithium battery before further processing.");
        }
        return sb.toString();
    }

    public boolean isHasBattery() {
        return hasBattery;
    }

    public void setHasBattery(boolean hasBattery) {
        this.hasBattery = hasBattery;
    }

    public boolean isHasHardDrive() {
        return hasHardDrive;
    }

    public void setHasHardDrive(boolean hasHardDrive) {
        this.hasHardDrive = hasHardDrive;
    }

    public double getScreenSizeInches() {
        return screenSizeInches;
    }

    public void setScreenSizeInches(double screenSizeInches) {
        this.screenSizeInches = screenSizeInches;
    }
}
