package com.ewaste.server.infrastructure.persistence.sqlite;

import com.ewaste.server.domain.model.collector.Collector;
import com.ewaste.server.domain.model.collector.VehicleType;
import com.ewaste.server.domain.repository.CollectorRepository;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class SqliteCollectorRepository implements CollectorRepository {

    private final DataSource dataSource;

    public SqliteCollectorRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<Collector> findById(Long collectorId) {
        String sql = "SELECT collector_id, user_id, vehicle_type, max_capacity_kg, current_workload_kg, is_available " +
                "FROM collectors WHERE collector_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, collectorId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find collector by ID", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Collector> findByUserId(Long userId) {
        String sql = "SELECT collector_id, user_id, vehicle_type, max_capacity_kg, current_workload_kg, is_available " +
                "FROM collectors WHERE user_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find collector by user ID", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Collector> findAll() {
        String sql = "SELECT collector_id, user_id, vehicle_type, max_capacity_kg, current_workload_kg, is_available FROM collectors";
        List<Collector> collectors = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                collectors.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find all collectors", e);
        }
        return collectors;
    }

    @Override
    public List<Collector> findByAvailability(boolean available) {
        String sql = "SELECT collector_id, user_id, vehicle_type, max_capacity_kg, current_workload_kg, is_available " +
                "FROM collectors WHERE is_available = ?";
        List<Collector> collectors = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, available ? 1 : 0);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    collectors.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find collectors by availability", e);
        }
        return collectors;
    }

    @Override
    public Collector save(Collector collector) {
        String sql = "INSERT INTO collectors (user_id, vehicle_type, max_capacity_kg, current_workload_kg, is_available) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, collector.getUserId());
            ps.setString(2, collector.getVehicleType() != null ? collector.getVehicleType().name() : VehicleType.VAN.name());
            ps.setDouble(3, collector.getMaxCapacityKg());
            ps.setDouble(4, collector.getCurrentWorkloadKg());
            ps.setInt(5, collector.isAvailable() ? 1 : 0);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    collector.setCollectorId(keys.getLong(1));
                }
            }
            return collector;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert collector", e);
        }
    }

    @Override
    public Collector update(Collector collector) {
        String sql = "UPDATE collectors SET vehicle_type = ?, max_capacity_kg = ?, current_workload_kg = ?, is_available = ? " +
                "WHERE collector_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, collector.getVehicleType() != null ? collector.getVehicleType().name() : VehicleType.VAN.name());
            ps.setDouble(2, collector.getMaxCapacityKg());
            ps.setDouble(3, collector.getCurrentWorkloadKg());
            ps.setInt(4, collector.isAvailable() ? 1 : 0);
            ps.setLong(5, collector.getCollectorId());
            ps.executeUpdate();
            return collector;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update collector", e);
        }
    }

    private Collector mapRow(ResultSet rs) throws SQLException {
        Collector c = new Collector();
        c.setCollectorId(rs.getLong("collector_id"));
        c.setUserId(rs.getLong("user_id"));
        c.setVehicleType(VehicleType.valueOf(rs.getString("vehicle_type")));
        c.setMaxCapacityKg(rs.getDouble("max_capacity_kg"));
        c.setCurrentWorkloadKg(rs.getDouble("current_workload_kg"));
        c.setAvailable(rs.getInt("is_available") == 1);
        return c;
    }
}