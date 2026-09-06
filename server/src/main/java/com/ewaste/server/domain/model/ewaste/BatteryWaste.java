package com.ewaste.server.domain.model.ewaste;

/** Standalone battery removed from a device. */
public class BatteryWaste extends EWasteItem {

    private static final double BASE_POINTS_PER_KG = 8.0;

    public enum BatteryType {
        LITHIUM_ION,
        NICKEL_METAL_HYDRIDE,
        LEAD_ACID,
        ALKALINE,
        OTHER
    }

    private BatteryType batteryType;
    private double capacityMah;
    private boolean swollenOrLeaking;

    public BatteryWaste(EWasteCategory category, String modelName, WasteCondition condition, double weightKg,
                        BatteryType batteryType, double capacityMah, boolean swollenOrLeaking) {
        super(category, modelName, condition, weightKg);
        this.batteryType = batteryType == null ? BatteryType.OTHER : batteryType;
        this.capacityMah = capacityMah;
        this.swollenOrLeaking = swollenOrLeaking;
    }

    @Override
    public double calculateRewardPoints() {
        double points = getWeightKg() * BASE_POINTS_PER_KG * getCondition().getRewardMultiplier();
        return Math.round(points * 100.0) / 100.0;
    }

    @Override
    public boolean isHazardous() {
        return true; // every battery is treated as chemically hazardous
    }

    @Override
    public String getProcessingInstructions() {
        if (swollenOrLeaking) {
            return "URGENT: swollen/leaking " + batteryType + " battery - isolate in a fire-safe container immediately.";
        }
        return "Store in the designated " + batteryType + " battery container pending hazardous disposal.";
    }

    public boolean isSwollenOrLeaking() {
        return swollenOrLeaking;
    }

    public void setSwollenOrLeaking(boolean swollenOrLeaking) {
        this.swollenOrLeaking = swollenOrLeaking;
    }

    public BatteryType getBatteryType() {
        return batteryType;
    }

    public void setBatteryType(BatteryType batteryType) {
        this.batteryType = batteryType;
    }

    public double getCapacityMah() {
        return capacityMah;
    }

    public void setCapacityMah(double capacityMah) {
        this.capacityMah = capacityMah;
    }
}
