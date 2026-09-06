package com.ewaste.server.infrastructure.persistence.sqlite;

import com.ewaste.server.domain.model.processing.RecyclingCenter;
import com.ewaste.server.domain.repository.RecyclingCenterRepository;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class SqliteRecyclingCenterRepository implements RecyclingCenterRepository {

    private final DataSource dataSource;

    public SqliteRecyclingCenterRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<RecyclingCenter> findById(Long centerId) {
        String sql = "SELECT center_id, center_name, address, processing_capacity_kg FROM recycling_centers WHERE center_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, centerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to query recycling center by ID", e);
        }
        return Optional.empty();
    }

    @Override
    public List<RecyclingCenter> findAll() {
        String sql = "SELECT center_id, center_name, address, processing_capacity_kg FROM recycling_centers";
        List<RecyclingCenter> centers = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                centers.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to query all recycling centers", e);
        }
        return centers;
    }

    @Override
    public RecyclingCenter save(RecyclingCenter center) {
        String sql = "INSERT INTO recycling_centers (center_name, address, processing_capacity_kg) VALUES (?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, center.getCenterName());
            ps.setString(2, center.getAddress());
            ps.setDouble(3, center.getCapacityKg());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    center.setCenterId(keys.getLong(1));
                }
            }
            return center;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert recycling center", e);
        }
    }

    @Override
    public RecyclingCenter update(RecyclingCenter center) {
        String sql = "UPDATE recycling_centers SET center_name = ?, address = ?, processing_capacity_kg = ? WHERE center_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, center.getCenterName());
            ps.setString(2, center.getAddress());
            ps.setDouble(3, center.getCapacityKg());
            ps.setLong(4, center.getCenterId());
            ps.executeUpdate();
            return center;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update recycling center", e);
        }
    }

    private RecyclingCenter mapRow(ResultSet rs) throws SQLException {
        RecyclingCenter center = new RecyclingCenter();
        center.setCenterId(rs.getLong("center_id"));
        center.setCenterName(rs.getString("center_name"));
        center.setAddress(rs.getString("address"));
        center.setCapacityKg(rs.getDouble("processing_capacity_kg"));
        return center;
    }
}