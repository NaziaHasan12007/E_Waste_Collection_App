package com.ewaste.server.domain.model.ewaste;

public enum WasteCondition {

    WORKING(1.0, "Fully functional"),
    MINOR_DAMAGE(0.7, "Functional with minor cosmetic or performance issues"),
    MAJOR_DAMAGE(0.4, "Significant damage, partially functional"),
    NON_FUNCTIONAL(0.2, "Does not power on or work at all"),
    SCRAP_ONLY(0.05, "Beyond repair, suitable only for material recovery");

    private final double rewardMultiplier;
    private final String description;

    WasteCondition(double rewardMultiplier, String description) {
        this.rewardMultiplier = rewardMultiplier;
        this.description = description;
    }

    public double getRewardMultiplier() {
        return rewardMultiplier;
    }

    public String getDescription() {
        return description;
    }

    /** Items in these conditions are worth attempting to fix rather than scrapping. */
    public boolean isRepairable() {
        return this == MINOR_DAMAGE || this == MAJOR_DAMAGE;
    }
}
