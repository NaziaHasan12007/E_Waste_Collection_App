package com.ewaste.server.domain.pattern.builder;

import com.ewaste.server.domain.model.ewaste.ApplianceWaste;

public class ApplianceWasteBuilder extends AbstractEWasteItemBuilder<ApplianceWaste, ApplianceWasteBuilder> {

    private ApplianceWaste.ApplianceType applianceType = ApplianceWaste.ApplianceType.OTHER;
    private boolean hasRefrigerant;
    private double powerRatingWatts;

    public ApplianceWasteBuilder applianceType(ApplianceWaste.ApplianceType applianceType) {
        this.applianceType = applianceType;
        return this;
    }

    public ApplianceWasteBuilder hasRefrigerant(boolean hasRefrigerant) {
        this.hasRefrigerant = hasRefrigerant;
        return this;
    }

    public ApplianceWasteBuilder powerRatingWatts(double powerRatingWatts) {
        this.powerRatingWatts = powerRatingWatts;
        return this;
    }

    @Override
    protected ApplianceWasteBuilder self() {
        return this;
    }

    @Override
    public ApplianceWaste build() {
        validateMandatory();
        return new ApplianceWaste(category, modelName, condition, weightKg, applianceType, hasRefrigerant,
                powerRatingWatts);
    }
}
