package com.ewaste.server.domain.model.user;


public enum Role {

    CUSTOMER("Customer", "Submits e-waste items, schedules pickups and tracks reward points"),
    COLLECTOR("Collector", "Collects assigned pickups and updates their status in the field"),
    ADMIN("Administrator", "Manages users/categories, configures dispatch, and runs facility inspections");

    private final String displayName;
    private final String description;

    Role(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public boolean isAdmin() {
        return this == ADMIN;
    }

    public boolean isCustomer() {
        return this == CUSTOMER;
    }

    public boolean isCollector() {
        return this == COLLECTOR;
    }

    /** Resolves a Role from a case-insensitive string (registration form, REST DTO, or DB row). */
    public static Role fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Role value cannot be null or blank");
        }
        for (Role role : values()) {
            if (role.name().equalsIgnoreCase(value.trim())) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown role: " + value);
    }
}
