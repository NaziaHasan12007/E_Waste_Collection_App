package com.ewaste.server.domain.pattern.factory;

import com.ewaste.server.domain.model.ewaste.EWasteItem;
import com.ewaste.server.domain.pattern.builder.ApplianceWasteBuilder;
import com.ewaste.server.domain.pattern.builder.BatteryWasteBuilder;
import com.ewaste.server.domain.pattern.builder.DisplayWasteBuilder;
import com.ewaste.server.domain.pattern.builder.LaptopWasteBuilder;
import com.ewaste.server.domain.pattern.builder.MobileWasteBuilder;
import com.ewaste.server.domain.model.ewaste.EWasteCategory;
import com.ewaste.server.domain.model.ewaste.BatteryWaste;
import com.ewaste.server.domain.model.ewaste.DisplayWaste;
import com.ewaste.server.domain.model.ewaste.ApplianceWaste;
import com.ewaste.server.domain.model.ewaste.WasteCondition;

import java.util.Locale;

public final class EWasteFactory {


    public enum EWasteType {
        LAPTOP,
        MOBILE,
        BATTERY,
        DISPLAY,
        APPLIANCE;

        public static EWasteType fromString(String value) {
            if (value == null || value.isBlank()) {
                throw new IllegalArgumentException("EWasteType value cannot be null or blank");
            }
            try {
                return EWasteType.valueOf(value.trim().toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Unknown e-waste type: " + value, ex);
            }
        }
    }

    private EWasteFactory() {
        // static factory only, not instantiable
    }

    /**
     * Reads {@link EWasteItemRequest#getType()} and delegates to the
     * builder that matches it, pulling only the fields that type actually
     * needs out of the request.
     */
    public static EWasteItem createEWasteItem(EWasteItemRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request is required");
        }
        if (request.getType() == null) {
            throw new IllegalArgumentException("request.type is required");
        }

        switch (request.getType()) {
            case LAPTOP:
                return new LaptopWasteBuilder()
                        .category(request.getCategory())
                        .modelName(request.getModelName())
                        .condition(request.getCondition())
                        .weightKg(request.getWeightKg())
                        .hasBattery(request.isHasBattery())
                        .hasHardDrive(request.isHasHardDrive())
                        .screenSizeInches(request.getScreenSizeInches())
                        .build();
            case MOBILE:
                return new MobileWasteBuilder()
                        .category(request.getCategory())
                        .modelName(request.getModelName())
                        .condition(request.getCondition())
                        .weightKg(request.getWeightKg())
                        .hasSimCard(request.isHasSimCard())
                        .storageGb(request.getStorageGb())
                        .build();
            case BATTERY:
                return new BatteryWasteBuilder()
                        .category(request.getCategory())
                        .modelName(request.getModelName())
                        .condition(request.getCondition())
                        .weightKg(request.getWeightKg())
                        .batteryType(request.getBatteryType())
                        .capacityMah(request.getCapacityMah())
                        .swollenOrLeaking(request.isSwollenOrLeaking())
                        .build();
            case DISPLAY:
                return new DisplayWasteBuilder()
                        .category(request.getCategory())
                        .modelName(request.getModelName())
                        .condition(request.getCondition())
                        .weightKg(request.getWeightKg())
                        .displayType(request.getDisplayType())
                        .screenSizeInches(request.getScreenSizeInches())
                        .build();
            case APPLIANCE:
                return new ApplianceWasteBuilder()
                        .category(request.getCategory())
                        .modelName(request.getModelName())
                        .condition(request.getCondition())
                        .weightKg(request.getWeightKg())
                        .applianceType(request.getApplianceType())
                        .hasRefrigerant(request.isHasRefrigerant())
                        .powerRatingWatts(request.getPowerRatingWatts())
                        .build();
            default:
                throw new IllegalArgumentException("Unsupported e-waste type: " + request.getType());
        }
    }

    /**
     * Flat request DTO used by {@link #createEWasteItem(EWasteItemRequest)}.
     * Holds every field any subtype might need; fields irrelevant to the
     * chosen {@link EWasteType} are simply ignored. Meant to be populated
     * from a REST request body ({@code SubmitEWasteRequest}) or an FXML
     * form ({@code SubmitEWasteController}).
     */
    public static class EWasteItemRequest {

        private EWasteType type;
        private EWasteCategory category;
        private String modelName;
        private WasteCondition condition;
        private double weightKg;

        // Laptop-specific (screenSizeInches shared with Display)
        private boolean hasBattery;
        private boolean hasHardDrive;
        private double screenSizeInches;

        // Mobile-specific
        private boolean hasSimCard;
        private int storageGb;

        // Battery-specific
        private BatteryWaste.BatteryType batteryType;
        private double capacityMah;
        private boolean swollenOrLeaking;

        // Display-specific
        private DisplayWaste.DisplayType displayType;

        // Appliance-specific
        private ApplianceWaste.ApplianceType applianceType;
        private boolean hasRefrigerant;
        private double powerRatingWatts;

        public EWasteType getType() { return type; }
        public EWasteItemRequest setType(EWasteType type) { this.type = type; return this; }

        public EWasteCategory getCategory() { return category; }
        public EWasteItemRequest setCategory(EWasteCategory category) { this.category = category; return this; }

        public String getModelName() { return modelName; }
        public EWasteItemRequest setModelName(String modelName) { this.modelName = modelName; return this; }

        public WasteCondition getCondition() { return condition; }
        public EWasteItemRequest setCondition(WasteCondition condition) { this.condition = condition; return this; }

        public double getWeightKg() { return weightKg; }
        public EWasteItemRequest setWeightKg(double weightKg) { this.weightKg = weightKg; return this; }

        public boolean isHasBattery() { return hasBattery; }
        public EWasteItemRequest setHasBattery(boolean hasBattery) { this.hasBattery = hasBattery; return this; }

        public boolean isHasHardDrive() { return hasHardDrive; }
        public EWasteItemRequest setHasHardDrive(boolean hasHardDrive) { this.hasHardDrive = hasHardDrive; return this; }

        public double getScreenSizeInches() { return screenSizeInches; }
        public EWasteItemRequest setScreenSizeInches(double screenSizeInches) { this.screenSizeInches = screenSizeInches; return this; }

        public boolean isHasSimCard() { return hasSimCard; }
        public EWasteItemRequest setHasSimCard(boolean hasSimCard) { this.hasSimCard = hasSimCard; return this; }

        public int getStorageGb() { return storageGb; }
        public EWasteItemRequest setStorageGb(int storageGb) { this.storageGb = storageGb; return this; }

        public BatteryWaste.BatteryType getBatteryType() { return batteryType; }
        public EWasteItemRequest setBatteryType(BatteryWaste.BatteryType batteryType) { this.batteryType = batteryType; return this; }

        public double getCapacityMah() { return capacityMah; }
        public EWasteItemRequest setCapacityMah(double capacityMah) { this.capacityMah = capacityMah; return this; }

        public boolean isSwollenOrLeaking() { return swollenOrLeaking; }
        public EWasteItemRequest setSwollenOrLeaking(boolean swollenOrLeaking) { this.swollenOrLeaking = swollenOrLeaking; return this; }

        public DisplayWaste.DisplayType getDisplayType() { return displayType; }
        public EWasteItemRequest setDisplayType(DisplayWaste.DisplayType displayType) { this.displayType = displayType; return this; }

        public ApplianceWaste.ApplianceType getApplianceType() { return applianceType; }
        public EWasteItemRequest setApplianceType(ApplianceWaste.ApplianceType applianceType) { this.applianceType = applianceType; return this; }

        public boolean isHasRefrigerant() { return hasRefrigerant; }
        public EWasteItemRequest setHasRefrigerant(boolean hasRefrigerant) { this.hasRefrigerant = hasRefrigerant; return this; }

        public double getPowerRatingWatts() { return powerRatingWatts; }
        public EWasteItemRequest setPowerRatingWatts(double powerRatingWatts) { this.powerRatingWatts = powerRatingWatts; return this; }
    }
}
