package com.ewaste.server.domain.pattern.builder;

import com.ewaste.server.domain.model.ewaste.DisplayWaste;

public class DisplayWasteBuilder extends AbstractEWasteItemBuilder<DisplayWaste, DisplayWasteBuilder> {

    private DisplayWaste.DisplayType displayType = DisplayWaste.DisplayType.LCD;
    private double screenSizeInches;

    public DisplayWasteBuilder displayType(DisplayWaste.DisplayType displayType) {
        this.displayType = displayType;
        return this;
    }

    public DisplayWasteBuilder screenSizeInches(double screenSizeInches) {
        this.screenSizeInches = screenSizeInches;
        return this;
    }

    @Override
    protected DisplayWasteBuilder self() {
        return this;
    }

    @Override
    public DisplayWaste build() {
        validateMandatory();
        return new DisplayWaste(category, modelName, condition, weightKg, displayType, screenSizeInches);
    }
}
