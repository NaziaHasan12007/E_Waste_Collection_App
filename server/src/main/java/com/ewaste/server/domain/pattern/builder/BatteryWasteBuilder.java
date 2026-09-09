package com.ewaste.server.domain.pattern.builder;

import com.ewaste.server.domain.model.ewaste.BatteryWaste;

public class BatteryWasteBuilder extends AbstractEWasteItemBuilder<BatteryWaste, BatteryWasteBuilder> {

    private BatteryWaste.BatteryType batteryType = BatteryWaste.BatteryType.OTHER;
    private double capacityMah;
    private boolean swollenOrLeaking;

    public BatteryWasteBuilder batteryType(BatteryWaste.BatteryType batteryType) {
        this.batteryType = batteryType;
        return this;
    }

    public BatteryWasteBuilder capacityMah(double capacityMah) {
        this.capacityMah = capacityMah;
        return this;
    }

    public BatteryWasteBuilder swollenOrLeaking(boolean swollenOrLeaking) {
        this.swollenOrLeaking = swollenOrLeaking;
        return this;
    }

    @Override
    protected BatteryWasteBuilder self() {
        return this;
    }

    @Override
    public BatteryWaste build() {
        validateMandatory();
        return new BatteryWaste(category, modelName, condition, weightKg, batteryType, capacityMah, swollenOrLeaking);
    }
}
