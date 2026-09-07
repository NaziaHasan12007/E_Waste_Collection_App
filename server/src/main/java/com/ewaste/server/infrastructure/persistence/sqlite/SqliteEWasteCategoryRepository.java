package com.ewaste.server.infrastructure.persistence.sqlite;

import com.ewaste.server.domain.model.ewaste.EWasteCategory;
import com.ewaste.server.domain.repository.EWasteCategoryRepository;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class SqliteEWasteCategoryRepository implements EWasteCategoryRepository {

    private final DatabaseConnectionManager connectionManager;

    public SqliteEWasteCategoryRepository(DatabaseConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public EWasteCategory save(EWasteCategory category) {
        String sql;
        boolean isUpdate = category.getCategoryId() != null;

        if (isUpdate) {
            sql = "UPDATE ewaste_categories SET category_name = ?, base_points_per_kg = ?, is_hazardous_default = ? WHERE category_id = ?";
        } else {
            sql = "INSERT INTO ewaste_categories (category_name, base_points_per_kg, is_hazardous_default) VALUES (?, ?, ?)";
        }

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, category.getCategoryName());
            stmt.setDouble(2, category.getBasePointsPerKg());
            stmt.setInt(3, category.isHazardousDefault() ? 1 : 0);

            if (isUpdate) {
                stmt.setLong(4, category.getCategoryId());
                stmt.executeUpdate();
            } else {
                stmt.executeUpdate();
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        category.setCategoryId(rs.getLong(1));
                    }
                }
            }

            return category;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save category: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<EWasteCategory> findById(Long categoryId) {
        String sql = "SELECT * FROM ewaste_categories WHERE category_id = ?";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, categoryId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCategory(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find category by ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<EWasteCategory> findByName(String categoryName) {
        String sql = "SELECT * FROM ewaste_categories WHERE category_name = ?";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, categoryName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToCategory(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find category by name: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<EWasteCategory> findAll() {
        String sql = "SELECT * FROM ewaste_categories ORDER BY category_name";
        List<EWasteCategory> categories = new ArrayList<>();

        try (Connection conn = connectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                categories.add(mapResultSetToCategory(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all categories: " + e.getMessage(), e);
        }
        return categories;
    }

    @Override
    public void deleteById(Long categoryId) {
        String sql = "DELETE FROM ewaste_categories WHERE category_id = ?";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, categoryId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete category: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existsByName(String categoryName) {
        String sql = "SELECT COUNT(*) FROM ewaste_categories WHERE category_name = ?";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, categoryName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check category name existence: " + e.getMessage(), e);
        }
        return false;
    }

    private EWasteCategory mapResultSetToCategory(ResultSet rs) throws SQLException {
        EWasteCategory category = new EWasteCategory();
        category.setCategoryId(rs.getLong("category_id"));
        category.setCategoryName(rs.getString("category_name"));
        category.setBasePointsPerKg(rs.getDouble("base_points_per_kg"));
        category.setHazardousDefault(rs.getInt("is_hazardous_default") == 1);
        return category;
    }
}