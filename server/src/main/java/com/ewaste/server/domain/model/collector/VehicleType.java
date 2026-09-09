package com.ewaste.server.domain.model.collector;

/**
 * Enum representing the type of vehicle a collector uses.
 */
public enum VehicleType {
    BIKE("Bike", "Small capacity, suitable for urban areas"),
    CAR("Car", "Medium capacity, suitable for suburban areas"),
    VAN("Van", "Large capacity, suitable for commercial areas"),
    TRUCK("Truck", "Extra large capacity, suitable for industrial areas"),
    ELECTRIC_VAN("Electric Van", "Eco-friendly, large capacity");

    private final String displayName;
    private final String description;

    VehicleType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public static VehicleType fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Vehicle type cannot be null or blank");
        }
        for (VehicleType type : values()) {
            if (type.name().equalsIgnoreCase(value.trim()) ||
                    type.displayName.equalsIgnoreCase(value.trim())) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown vehicle type: " + value);
    }

    @Override
    public String toString() {
        return displayName;
    }
}