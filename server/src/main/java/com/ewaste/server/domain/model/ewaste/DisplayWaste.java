package com.ewaste.server.domain.model.ewaste;

/** Monitor / TV display. */
public class DisplayWaste extends EWasteItem {

    private static final double BASE_POINTS_PER_KG = 10.0;

    public enum DisplayType {
        CRT,
        LCD,
        LED,
        PLASMA,
        OLED
    }

    private DisplayType displayType;
    private double screenSizeInches;

    public DisplayWaste(EWasteCategory category, String modelName, WasteCondition condition, double weightKg,
                        DisplayType displayType, double screenSizeInches) {
        super(category, modelName, condition, weightKg);
        this.displayType = displayType == null ? DisplayType.LCD : displayType;
        this.screenSizeInches = screenSizeInches;
    }

    @Override
    public double calculateRewardPoints() {
        double points = getWeightKg() * BASE_POINTS_PER_KG * getCondition().getRewardMultiplier();
        return Math.round(points * 100.0) / 100.0;
    }

    @Override
    public boolean isHazardous() {
        return displayType == DisplayType.CRT; // leaded glass
    }

    @Override
    public String getProcessingInstructions() {
        if (displayType == DisplayType.CRT) {
            return "Handle with care: leaded glass tube requires certified hazardous disposal.";
        }
        return "Dismantle the panel and recover backlight/circuit components for recycling.";
    }

    public DisplayType getDisplayType() {
        return displayType;
    }

    public void setDisplayType(DisplayType displayType) {
        this.displayType = displayType;
    }

    public double getScreenSizeInches() {
        return screenSizeInches;
    }

    public void setScreenSizeInches(double screenSizeInches) {
        this.screenSizeInches = screenSizeInches;
    }
}
