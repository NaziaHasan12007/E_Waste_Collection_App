package com.ewaste.server.infrastructure.persistence.sqlite;

import com.ewaste.server.domain.model.ewaste.ApplianceWaste;
import com.ewaste.server.domain.model.ewaste.BatteryWaste;
import com.ewaste.server.domain.model.ewaste.DisplayWaste;
import com.ewaste.server.domain.model.ewaste.EWasteCategory;
import com.ewaste.server.domain.model.ewaste.EWasteItem;
import com.ewaste.server.domain.model.ewaste.LaptopWaste;
import com.ewaste.server.domain.model.ewaste.MobileWaste;
import com.ewaste.server.domain.model.ewaste.WasteCondition;
import com.ewaste.server.domain.repository.EWasteItemRepository;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class SqliteEWasteItemRepository implements EWasteItemRepository {

    private final DatabaseConnectionManager connectionManager;

    public SqliteEWasteItemRepository(DatabaseConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public EWasteItem save(EWasteItem item) {

        String sql;
        boolean isUpdate = item.getId() != null;

        if (isUpdate) {
            sql = "UPDATE ewaste_items SET category_id = ?, model_name = ?, weight_kg = ?, " +
                    "is_hazardous = ?, waste_condition = ?, specific_attributes = ? " +
                    "WHERE item_id = ?";
        } else {
            sql = "INSERT INTO ewaste_items " +
                    "(category_id, model_name, weight_kg, is_hazardous, waste_condition, specific_attributes) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        }

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     sql,
                     Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, item.getCategory().getId());
            stmt.setString(2, item.getModelName());
            stmt.setDouble(3, item.getWeightKg());

            // Hazardous status comes from the concrete EWasteItem subtype
            stmt.setInt(4, item.isHazardous() ? 1 : 0);

            // WasteCondition is an enum
            stmt.setString(5, item.getCondition().name());

            // Store subtype-specific attributes as text
            stmt.setString(6, item.getDescription() != null && !item.getDescription().isBlank()
                    ? item.getDescription() : buildSpecificAttributes(item));

            if (isUpdate) {
                stmt.setLong(7, item.getId());
                stmt.executeUpdate();
            } else {
                stmt.executeUpdate();

                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        item.setId(rs.getLong(1));
                    }
                }
            }

            return item;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to save e-waste item: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<EWasteItem> findById(Long itemId) {

        String sql = "SELECT ei.*, " +
                "ec.category_name, ec.base_points_per_kg, ec.is_hazardous_default " +
                "FROM ewaste_items ei " +
                "JOIN ewaste_categories ec ON ei.category_id = ec.category_id " +
                "WHERE ei.item_id = ?";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, itemId);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    return Optional.of(mapResultSetToItem(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to find item by ID: " + e.getMessage(), e);
        }

        return Optional.empty();
    }

    @Override
    public List<EWasteItem> findAll() {

        String sql = "SELECT ei.*, " +
                "ec.category_name, ec.base_points_per_kg, ec.is_hazardous_default " +
                "FROM ewaste_items ei " +
                "JOIN ewaste_categories ec ON ei.category_id = ec.category_id " +
                "ORDER BY ei.item_id";

        List<EWasteItem> items = new ArrayList<>();

        try (Connection conn = connectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                items.add(mapResultSetToItem(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to find all items: " + e.getMessage(), e);
        }

        return items;
    }

    @Override
    public List<EWasteItem> findByCategoryId(Long categoryId) {

        String sql = "SELECT ei.*, " +
                "ec.category_name, ec.base_points_per_kg, ec.is_hazardous_default " +
                "FROM ewaste_items ei " +
                "JOIN ewaste_categories ec ON ei.category_id = ec.category_id " +
                "WHERE ei.category_id = ?";

        List<EWasteItem> items = new ArrayList<>();

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, categoryId);

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    items.add(mapResultSetToItem(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to find items by category: " + e.getMessage(), e);
        }

        return items;
    }

    @Override
    public List<EWasteItem> findByHazardousStatus(boolean isHazardous) {

        String sql = "SELECT ei.*, " +
                "ec.category_name, ec.base_points_per_kg, ec.is_hazardous_default " +
                "FROM ewaste_items ei " +
                "JOIN ewaste_categories ec ON ei.category_id = ec.category_id " +
                "WHERE ei.is_hazardous = ?";

        List<EWasteItem> items = new ArrayList<>();

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, isHazardous ? 1 : 0);

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    items.add(mapResultSetToItem(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to find items by hazard status: " + e.getMessage(), e);
        }

        return items;
    }

    @Override
    public List<EWasteItem> findByPickupId(Long pickupId) {

        String sql = "SELECT ei.*, " +
                "ec.category_name, ec.base_points_per_kg, ec.is_hazardous_default " +
                "FROM ewaste_items ei " +
                "JOIN pickup_items pi ON ei.item_id = pi.item_id " +
                "JOIN ewaste_categories ec ON ei.category_id = ec.category_id " +
                "WHERE pi.pickup_id = ?";

        List<EWasteItem> items = new ArrayList<>();

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, pickupId);

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    items.add(mapResultSetToItem(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to find items by pickup ID: " + e.getMessage(), e);
        }

        return items;
    }

    @Override
    public void deleteById(Long itemId) {

        String sql = "DELETE FROM ewaste_items WHERE item_id = ?";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, itemId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to delete item: " + e.getMessage(), e);
        }
    }

    @Override
    public double calculateTotalWeightByPickupId(Long pickupId) {

        String sql = "SELECT SUM(ei.weight_kg) " +
                "FROM ewaste_items ei " +
                "JOIN pickup_items pi ON ei.item_id = pi.item_id " +
                "WHERE pi.pickup_id = ?";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, pickupId);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to calculate total weight: " + e.getMessage(), e);
        }

        return 0.0;
    }

    /**
     * Converts one database row into the correct concrete EWasteItem subtype.
     */
    private EWasteItem mapResultSetToItem(ResultSet rs) throws SQLException {

        Long itemId = rs.getLong("item_id");
        Long categoryId = rs.getLong("category_id");

        String categoryName = rs.getString("category_name");
        double basePointsPerKg = rs.getDouble("base_points_per_kg");
        boolean hazardousDefault =
                rs.getInt("is_hazardous_default") == 1;

        EWasteCategory category = new EWasteCategory(
                categoryName,
                basePointsPerKg,
                hazardousDefault
        );

        category.setId(categoryId);

        String modelName = rs.getString("model_name");
        double weightKg = rs.getDouble("weight_kg");

        WasteCondition condition =
                WasteCondition.valueOf(
                        rs.getString("waste_condition")
                );

        String attributes = rs.getString("specific_attributes");

        String type = categoryName.trim().toUpperCase();

        EWasteItem item;

        switch (type) {

            case "LAPTOP":

                item = new LaptopWaste(
                        category,
                        modelName,
                        condition,
                        weightKg,
                        getBooleanAttribute(attributes, "hasBattery"),
                        getBooleanAttribute(attributes, "hasHardDrive"),
                        getDoubleAttribute(attributes, "screenSizeInches")
                );

                break;

            case "MOBILE PHONE":
            case "MOBILE":

                item = new MobileWaste(
                        category,
                        modelName,
                        condition,
                        weightKg,
                        getBooleanAttribute(attributes, "hasSimCard"),
                        getIntAttribute(attributes, "storageGb")
                );

                break;

            case "BATTERY":

                item = new BatteryWaste(
                        category,
                        modelName,
                        condition,
                        weightKg,
                        getEnumAttribute(
                                attributes,
                                "batteryType",
                                BatteryWaste.BatteryType.OTHER
                        ),
                        getDoubleAttribute(attributes, "capacityMah"),
                        getBooleanAttribute(attributes, "swollenOrLeaking")
                );

                break;

            case "DISPLAY":

                item = new DisplayWaste(
                        category,
                        modelName,
                        condition,
                        weightKg,
                        getEnumAttribute(
                                attributes,
                                "displayType",
                                DisplayWaste.DisplayType.LCD
                        ),
                        getDoubleAttribute(attributes, "screenSizeInches")
                );

                break;

            case "APPLIANCE":

                item = new ApplianceWaste(
                        category,
                        modelName,
                        condition,
                        weightKg,
                        getEnumAttribute(
                                attributes,
                                "applianceType",
                                ApplianceWaste.ApplianceType.OTHER
                        ),
                        getBooleanAttribute(attributes, "hasRefrigerant"),
                        getDoubleAttribute(attributes, "powerRatingWatts")
                );

                break;

            default:
                throw new IllegalArgumentException(
                        "Unknown e-waste category: " + categoryName
                );
        }

        item.setId(itemId);
        item.setDescription(attributes);

        return item;
    }

    /**
     * Creates a simple JSON-like string containing subtype-specific fields.
     */
    private String buildSpecificAttributes(EWasteItem item) {

        if (item instanceof LaptopWaste laptop) {

            return "{"
                    + "\"hasBattery\":" + laptop.isHasBattery() + ","
                    + "\"hasHardDrive\":" + laptop.isHasHardDrive() + ","
                    + "\"screenSizeInches\":" + laptop.getScreenSizeInches()
                    + "}";

        } else if (item instanceof MobileWaste mobile) {

            return "{"
                    + "\"hasSimCard\":" + mobile.isHasSimCard() + ","
                    + "\"storageGb\":" + mobile.getStorageGb()
                    + "}";

        } else if (item instanceof BatteryWaste battery) {

            return "{"
                    + "\"batteryType\":\"" + battery.getBatteryType() + "\","
                    + "\"capacityMah\":" + battery.getCapacityMah() + ","
                    + "\"swollenOrLeaking\":" + battery.isSwollenOrLeaking()
                    + "}";

        } else if (item instanceof DisplayWaste display) {

            return "{"
                    + "\"displayType\":\"" + display.getDisplayType() + "\","
                    + "\"screenSizeInches\":" + display.getScreenSizeInches()
                    + "}";

        } else if (item instanceof ApplianceWaste appliance) {

            return "{"
                    + "\"applianceType\":\"" + appliance.getApplianceType() + "\","
                    + "\"hasRefrigerant\":" + appliance.isHasRefrigerant() + ","
                    + "\"powerRatingWatts\":" + appliance.getPowerRatingWatts()
                    + "}";
        }

        return null;
    }

    private boolean getBooleanAttribute(String attributes, String key) {

        String value = getAttribute(attributes, key);

        return value != null && Boolean.parseBoolean(value);
    }

    private double getDoubleAttribute(String attributes, String key) {

        String value = getAttribute(attributes, key);

        if (value == null || value.isBlank()) {
            return 0.0;
        }

        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private int getIntAttribute(String attributes, String key) {

        String value = getAttribute(attributes, key);

        if (value == null || value.isBlank()) {
            return 0;
        }

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private <E extends Enum<E>> E getEnumAttribute(
            String attributes,
            String key,
            E defaultValue) {

        String value = getAttribute(attributes, key);

        if (value == null || value.isBlank()) {
            return defaultValue;
        }

        try {
            return Enum.valueOf(
                    defaultValue.getDeclaringClass(),
                    value
            );
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }

    /**
     * Reads a value from our simple JSON-like attributes string.
     */
    private String getAttribute(String attributes, String key) {

        if (attributes == null || attributes.isBlank()) {
            return null;
        }

        String searchKey = "\"" + key + "\":";
        int start = attributes.indexOf(searchKey);

        if (start == -1) {
            return null;
        }

        start += searchKey.length();

        while (start < attributes.length()
                && Character.isWhitespace(attributes.charAt(start))) {
            start++;
        }

        if (start >= attributes.length()) {
            return null;
        }

        // String value
        if (attributes.charAt(start) == '"') {

            int end = attributes.indexOf('"', start + 1);

            if (end == -1) {
                return null;
            }

            return attributes.substring(start + 1, end);
        }

        // Boolean / number value
        int end = start;

        while (end < attributes.length()
                && attributes.charAt(end) != ','
                && attributes.charAt(end) != '}') {
            end++;
        }

        return attributes.substring(start, end).trim();
    }
}