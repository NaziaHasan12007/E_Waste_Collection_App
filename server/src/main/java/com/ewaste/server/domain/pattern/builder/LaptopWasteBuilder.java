package com.ewaste.server.domain.pattern.builder;

import com.ewaste.server.domain.model.ewaste.LaptopWaste;

public class LaptopWasteBuilder extends AbstractEWasteItemBuilder<LaptopWaste, LaptopWasteBuilder> {

    private boolean hasBattery;
    private boolean hasHardDrive;
    private double screenSizeInches;

    public LaptopWasteBuilder hasBattery(boolean hasBattery) {
        this.hasBattery = hasBattery;
        return this;
    }

    public LaptopWasteBuilder hasHardDrive(boolean hasHardDrive) {
        this.hasHardDrive = hasHardDrive;
        return this;
    }

    public LaptopWasteBuilder screenSizeInches(double screenSizeInches) {
        this.screenSizeInches = screenSizeInches;
        return this;
    }

    @Override
    protected LaptopWasteBuilder self() {
        return this;
    }

    @Override
    public LaptopWaste build() {
        validateMandatory();
        return new LaptopWaste(category, modelName, condition, weightKg, hasBattery, hasHardDrive, screenSizeInches);
    }
}
